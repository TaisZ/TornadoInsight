package com.tais.tornado_plugins.mockExecution;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.CapturingProcessHandler;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.execution.util.ExecUtil;
import com.intellij.ide.BrowserUtil;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.JavaSdkType;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.PsiMethod;
import com.tais.tornado_plugins.ui.settings.TornadoSettingState;
import com.tais.tornado_plugins.util.MessageUtils;
import com.tais.tornado_plugins.util.TornadoTWTask;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class ExecutionEngine {

    private final String tempFolderPath;

    private final HashMap<String, PsiMethod> fileMethodMap;

    private final Project project;

    public ExecutionEngine(Project project, String tempFolderPath, HashMap<String, PsiMethod> fileMethodMap) {
        this.project = project;
        this.tempFolderPath = tempFolderPath;
        this.fileMethodMap = fileMethodMap;
    }

    public void run() {
        // Performing UI related operations on a non-EDT is not allowed.
        // To ensure that the code executes on EDT, need use Application.invokeLater().
        Application application = ApplicationManager.getApplication();
        MessageUtils.getInstance(project).showInfoMsg("Dynamic Testing", "Starting Test...");
        application.executeOnPooledThread(() -> {
            ArrayList<String> files = new ArrayList<>(fileMethodMap.keySet());
            try {
                compile(tempFolderPath, files);
                packFolder(tempFolderPath, tempFolderPath);
                executeJars(tempFolderPath);
            } catch (ExecutionException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private String getJavacPath(Project project) {
        Sdk sdk = ProjectRootManager.getInstance(project).getProjectSdk();
        if (sdk != null && sdk.getSdkType() instanceof JavaSdkType) {
            return ((JavaSdkType) sdk.getSdkType()).getBinPath(sdk) + (System.getProperty("os.name").startsWith("Win") ? "\\javac.exe" : "/javac");
        }
        return null;  // Consider throwing an exception if no appropriate JDK is found.
    }

    private void compile(String outputDir, ArrayList<String> javaFiles) throws ExecutionException {
        MessageUtils.getInstance(project).showInfoMsg("Dynamic Testing", "Compiling test files...");
        GeneralCommandLine commandLine = new GeneralCommandLine();
        System.out.println(TornadoSettingState.getInstance().Java21);
        commandLine.setExePath(TornadoSettingState.getInstance().Java21 + "/javac");
        commandLine.addParameter("--release");
        commandLine.addParameter("21");
        commandLine.addParameter("--enable-preview");
        commandLine.addParameter("-g");
        commandLine.addParameter("-classpath");
        commandLine.addParameter(TornadoSettingState.getInstance().getApiPath()+
                File.pathSeparator + TornadoSettingState.getInstance().getMatricesPath()) ;
        commandLine.addParameter("-d");
        commandLine.addParameter(outputDir);
        commandLine.addParameters(javaFiles);  // Adds each Java file to the command line

        // Execute the command
        try {
            System.out.println(ExecUtil.execAndGetOutput(commandLine));
        } catch (ExecutionException e) {
            MessageUtils.getInstance(project).showErrorMsg("Dynamic Testing",
                    "Compilation failure, may be JAVA_HOME is not correctly identified or " +
                    "there are temporarily unsupported data types");
        }
    }

    private void packFolder(String classFolderPath, String outputFolderPath) throws IOException {
        MessageUtils.getInstance(project).showInfoMsg("Dynamic Testing", "Packing test files...");
        File classFolder = new File(classFolderPath);
        File[] classFiles = classFolder.listFiles((dir, name) -> name.endsWith(".class"));
        if (classFiles == null) {
            System.out.println("No .class files found in the specified input folder.");
            return;
        }

        File outputFolder = new File(outputFolderPath);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }

        for (File classFile : classFiles) {
            Manifest manifest = new Manifest();
            manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
            manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, classFile.getName().replace(".class", ""));

            File outputJar = new File(outputFolder, classFile.getName().replace(".class", ".jar"));

            try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(outputJar), manifest)) {
                Path classPath = classFile.toPath();
                jos.putNextEntry(new JarEntry(classPath.getFileName().toString()));
                Files.copy(classPath, jos);
                jos.closeEntry();
            } catch (IOException e) {
                MessageUtils.getInstance(project).showErrorMsg("Dynamic Testing", "Failed to package test files");
            }
        }
    }

    private void executeJars(String jarFolderPath) {
        MessageUtils.getInstance(project).showInfoMsg("Dynamic Testing", "Tests are being executed...");
        GeneralCommandLine commandLine = new GeneralCommandLine();
        //Detecting if the user has correctly installed TornadoVM
        String sourceFile = TornadoSettingState.getInstance().setVarsPath();
        commandLine.setExePath("/bin/sh");
        commandLine.addParameter("-c");
        commandLine.addParameter("source " + sourceFile + ";tornado --device");
        try {
            CapturingProcessHandler handler = new CapturingProcessHandler(commandLine);
            ProcessOutput output = handler.runProcess();
            if (output.getExitCode() != 0) {
                // TornadoVM is not properly installed on the user's machine
                Notification notification = new Notification("Print", "TornadoVM not detected",
                        "TornadoVM is not properly installed or configured", NotificationType.ERROR);
                notification.addAction(new NotificationAction("How to install and configure TornadoVM") {
                    @Override
                    public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                        BrowserUtil.browse("https://tornadovm.readthedocs.io/en/latest/installation.html#");
                    }
                });
                Notifications.Bus.notify(notification);
                return;
            }
        } catch (ExecutionException ignored) {
            MessageUtils.getInstance(project).showErrorMsg("Dynamic Testing",
                    "TornadoVM environment variable file is not set correctly.");

            return;
        }

        File folder = new File(jarFolderPath);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null) {
            System.out.println("No files found in the specified directory.");
            return;
        }

        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().endsWith(".jar")) {
                runTornadoOnJar(file.getAbsolutePath());
            }
        }
    }

    private void runTornadoOnJar(String jarPath) {
        String sourceFile = TornadoSettingState.getInstance().setVarsPath();
        GeneralCommandLine commandLine = new GeneralCommandLine();
        commandLine.setExePath("/bin/sh");
        commandLine.addParameter("-c");
        commandLine.addParameter("source " + sourceFile + ";tornado --debug --printKernel -jar " + jarPath);
        try {
            CapturingProcessHandler handler = new CapturingProcessHandler(commandLine);
            ProcessOutput output = handler.runProcess();
            //Cannot use the exit code to determine if TornadoVM is running with an error or not.
            System.out.println(output);
            // Under normal circumstances Tornado output will also have error output For example:
            // " WARNING: Using incubator modules: jdk.incubator.foreign, jdk.incubator.vector "
            printResults(jarPath, output.toString().contains("Exception"), output);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    //Statistics of test results for each method
    private void printResults(String jarPath, boolean hasException, ProcessOutput output) {
        String javaPath = jarPath.substring(0, jarPath.lastIndexOf(".jar")) + ".java";
        ApplicationManager.getApplication().runReadAction(() -> {
            String methodName = TornadoTWTask.psiMethodFormat(fileMethodMap.get(javaPath));
            if (hasException) {
                MessageUtils consoleInstance = MessageUtils.getInstance(project);
                consoleInstance.showErrorMsg("Dynamic Testing",methodName + ": " + output.getStderr());
//                consoleInstance.showInfoMsg("Dynamic Testing",
//                        "Test assigning values to variables using default values");
//                consoleView.printHyperlink("customise your assignments\n"
//                                , project -> Messages.showMessageDialog(project,
//                                        "This dialogue box will be reserved for the user to change the assigned value.",
//                                        "Dialog Title", Messages.getInformationIcon()));
                consoleInstance.showInfoMsg("Dynamic Testing","Please visit the TornadoVM docs for more info: " +
                                        "https://tornadovm.readthedocs.io/en/latest/unsupported.html"+"\n");
                consoleInstance.showInfoMsg("Dynamic Testing","Got a bug? Report it to TornadoVM team: " +
                                        "https://github.com/beehive-lab/TornadoVM/issues"+"\n");
            } else {
                MessageUtils.getInstance(project).showInfoMsg("Dynamic Testing",methodName + ": " + "Your method has no exceptions\n" );
                MessageUtils.getInstance(project).showInfoMsg("OpenCL Kernel", output.getStdout());
            }
        });
    }
}
