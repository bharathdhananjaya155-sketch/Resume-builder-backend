package com.Project.ResumeBuilder.util;

import javax.xml.crypto.dsig.spec.XSLTTransformParameterSpec;
import java.util.SplittableRandom;

public class AppConstant {

    public static final String AUTH_CONTROLLER = "/api/auth";
    public static final String REGISTER = "/register";
    public static final String VERIFY_EMAIL = "/verify-email";
    public static final String UPLOAD_IMAGE="/upload-image";
    public static final String LOGIN="/login";
    public static final String RESEND_VERIFICATION="/resend-verification";
    public static final String PROFILE="/profile";
    public static final String RESUME_CONTROLLER="/api/resumes";
    public static final String ID="/{id}";
    public static final String UPLOAD_IMAGES="/{id}/upload-images";
    public static final String EXPORT_PDF="/{id}/export/pdf";
    public static final String TEMPLATE_CONTROLLER="/api/templates";
    public static final String PREMIUM="premium";
    public static final String PAYMENT_CONTROLLER="/api/payments";
    public static final String CREATE_ORDER="/create-order";
    public static final String VERIFY_PAYMENT="/verify-payment";
    public static final String HISTORY="/history";
    public static final String ORDER="/order/{orderId}";
}
