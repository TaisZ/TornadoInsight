package com.tais.tornado_plugins.entity;

import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;

import java.util.ArrayList;

/**
 * Represents a method entity with additional attributes for dynamic inspection processing.
 * This entity captures the method details, the parameter values,
 * and the classification of parameters for device/host transfer.
 */
public class Method {
    private final PsiMethod method; // The method reference from PSI (Program Structure Interface)
    private final ArrayList<String> parameterValues; // The values of the method's parameters

    // Parameters that need to be transferred to the device
    private final ArrayList<PsiParameter> toDeviceParameters;

    // Parameters that need to be transferred back to the host
    private final ArrayList<PsiParameter> toHostParameters;

    /**
     * Constructor for creating a Method instance with basic details.
     *
     * @param method           The PSI representation of the method.
     * @param parameterValues  The values of the method's parameters.
     */
    public Method(PsiMethod method, ArrayList<String> parameterValues) {
        this.method = method;
        this.parameterValues = parameterValues;
        this.toDeviceParameters = new ArrayList<>();
        this.toHostParameters = new ArrayList<>();
    }

    /**
     * Constructor for creating a Method instance with comprehensive details.
     *
     * @param method               The PSI representation of the method.
     * @param parameterValues      The values of the method's parameters.
     * @param toDeviceParameters   Parameters that need to be transferred to the device.
     * @param toHostParameters     Parameters that need to be transferred back to the host.
     */
    public Method(PsiMethod method, ArrayList<String> parameterValues, ArrayList<PsiParameter> defaultParameters,
                  ArrayList<PsiParameter> toDeviceParameters, ArrayList<PsiParameter> toHostParameters) {
        this.method = method;
        this.parameterValues = parameterValues;
        this.toDeviceParameters = toDeviceParameters;
        this.toHostParameters = toHostParameters;
    }

    /**
     * @return The PSI representation of the method.
     */
    public PsiMethod getMethod() {
        return method;
    }

    /**
     * @return The values of the method's parameters.
     */
    public ArrayList<String> getParameterValues() {
        return parameterValues;
    }

    /**
     * @return List of parameters that need to be transferred to the device.
     */
    public ArrayList<PsiParameter> getToDeviceParameters() {
        return toDeviceParameters;
    }

    /**
     * @return List of parameters that need to be transferred back to the host.
     */
    public ArrayList<PsiParameter> getToHostParameters() {
        return toHostParameters;
    }

    public String makePublicStatic(){
        // Check if the method is already static or public
        String methodStr = method.getText();
        boolean isStatic = methodStr.contains("static");
        boolean isPublic = methodStr.contains("public");

        // Replace or prepend modifiers as necessary
        if (!isStatic && !isPublic) {
            return methodStr.replaceFirst("\\b(\\w+\\s+\\w+\\()", "public static $1");
        } else if (!isStatic) {
            return methodStr.replaceFirst("public", "public static");
        } else if (!isPublic) {
            return methodStr.replaceFirst("static", "public static");
        }
        // If already static public, return the original string
        return methodStr;
    }
}
