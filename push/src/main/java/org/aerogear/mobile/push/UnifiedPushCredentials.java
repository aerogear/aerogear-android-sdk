package org.aerogear.mobile.push;

public final class UnifiedPushCredentials {

    private String variant;
    private String secret;
    private String sender;

    /**
     * ID of the Variant from the AeroGear UnifiedPush Server.
     *
     * @return the current variant id
     */
    public String getVariant() {
        return variant;
    }

    /**
     * ID of the Variant from the AeroGear UnifiedPush Server.
     *
     * @param variant the new variantID
     */
    public void setVariant(String variant) {
        this.variant = variant;
    }

    /**
     * Secret of the Variant from the AeroGear UnifiedPush Server.
     *
     * @return the current Secret
     */
    public String getSecret() {
        return secret;
    }

    /**
     * Secret of the Variant from the AeroGear UnifiedPush Server.
     *
     * @param secret the new secret
     */
    public void setSecret(String secret) {
        this.secret = secret;
    }

    /**
     * Firebase sender Id registered for this application.
     */
    public String getSender() {
        return sender;
    }

    /**
     * Firebase sender Id registered for this application.
     *
     * @param sender the new senderId
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

}
