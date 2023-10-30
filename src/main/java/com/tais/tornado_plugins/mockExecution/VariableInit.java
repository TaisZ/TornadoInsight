package com.tais.tornado_plugins.mockExecution;

import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import com.tais.tornado_plugins.ui.settings.TornadoSettingState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Random;

public class VariableInit {

    public static int parameterSize;


    public static String variableInitHelper(@NotNull PsiMethod method) {
        parameterSize = TornadoSettingState.getInstance().parameterSize;
        ArrayList<String> parametersName = new ArrayList<>();
        ArrayList<String> parametersType = new ArrayList<>();
        for (PsiParameter parameter : method.getParameterList().getParameters()) {
            PsiType type = parameter.getType();
            parametersType.add(parameter.getTypeElement().getText());
            parametersName.add(parameter.getName());
        }
        return variableInit(parametersName,parametersType);
    }

    private static String variableInit(@NotNull ArrayList<String> parametersName, ArrayList<String> parametersType){
        StringBuilder returnString = new StringBuilder();
        int size = parametersName.size();
        for (int i = 0; i < size; i++) {
            returnString.append(parametersType.get(i)).append(" ").append(parametersName.get(i));
            String value = lookupBoxedTypes(parametersType.get(i), parametersName.get(i), parameterSize);
            returnString.append(value);
        }
        return returnString.toString();
    }

    //todo: add more datatype init;
    private static String lookupBoxedTypes(String type, String name, int size){
        System.out.println(type);
        return switch (type) {
            case "int" -> "=" + generateValueByType("Int") + ";";
            case "float" -> "=" + generateValueByType("Float") + ";";
            case "double" -> "=" + generateValueByType("Double") + ";";
            case "IntArray" -> "= new IntArray(" + size + ");" + name + ".init(" + generateValueByType("Int") + ");";
            case "DoubleArray" ->
                    "= new DoubleArray(" + size + ");" + name + ".init(" + generateValueByType("Double") + ");";
            case "FloatArray" ->
                    "= new FloatArray(" + size + ");" + name + ".init(" + generateValueByType("Float") + "f);";
            case "Matrix2DFloat", "Matrix2DDouble", "Matrix2DInt" -> matrix2DInit(type, name);
            default -> "";
        };
    }

    private static String matrix2DInit(String type, String name){
        StringBuilder builder = new StringBuilder();
        builder.append("=new ").append(type).append("(").append(parameterSize).append(",").append(parameterSize).append(");");
        builder.append("for (int i = 0; i <" + parameterSize + "; i++) { "+
            "for (int j = 0; j < " + parameterSize +"; j++) {" +
                name + ".set(i, j, " + generateValueByType(type.split("Matrix2D")[1]) + ")" +
            "}" +
        "}");
        return builder.toString();
    }

    private static String generateValueByType(String type){
        Random r = new Random();
        return switch (type) {
            case "Int" -> "" + r.nextInt(1000);
            case "Float" -> "" + r.nextFloat(1000);
            case "Double" -> "" + r.nextDouble(1000);
            default -> "";
        };
    }

    public static void setParameterSize(int size){
       parameterSize = size;
    }
}
