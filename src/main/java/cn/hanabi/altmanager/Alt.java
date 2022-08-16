package cn.hanabi.altmanager;


public final class Alt {
    private String username;
    private String mask;
    private String password;
    public double anim = 0d;
    public double loginAnim = 0d;

    public Alt(final String username, final String password) {
        this(username, password, "");
    }

    public Alt(final String username, final String password, final String mask) {
        this.mask = "";
        this.username = username;
        this.password = password;
        this.mask = mask;
    }

    public String getMask() {
        return this.mask;
    }

    public void setMask(final String mask) {
        this.mask = mask;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }
}
