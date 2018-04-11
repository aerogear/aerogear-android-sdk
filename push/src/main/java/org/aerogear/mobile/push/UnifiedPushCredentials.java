package org.aerogear.mobile.push;

public final class UnifiedPushCredentials {

    private String variant;
    private String secret;
    private String senderId;

    /**
     * ID of the variant from the AeroGear UnifiedPush Server.
     *
     * @return the current variant id
     */
    public String getVariant() {
        return variant;
    }

    /**
     * ID of the variant from the AeroGear UnifiedPush Server.
     *
     * @param variant the new variantID
     */
    public void setVariant(String variant) {
        this.variant = variant;
    }

    /**
     * Secret for the variant from the AeroGear UnifiedPush Server.
     *
     * @return the current Secret
     */
    public String getSecret() {
        return secret;
    }

    /**
     * Secret for the variant from the AeroGear UnifiedPush Server.
     *
     * @param secret the new secret
     */
    public void setSecret(String secret) {
        this.secret = secret;
    }

    /**
     * Firebase senderId registered for this application.
     * 
     * @return senderId
     */
    public String getSenderId() {
        return senderId;
    }

    /**
     * Firebase senderId registered for this application.
     *
     * @param senderId the new senderId
     */
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

}
