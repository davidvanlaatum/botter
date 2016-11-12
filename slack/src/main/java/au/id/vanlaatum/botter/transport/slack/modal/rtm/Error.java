package au.id.vanlaatum.botter.transport.slack.modal.rtm;

public class Error extends BaseEvent {
  private SubError error;

  public SubError getError () {
    return error;
  }

  public void setError ( SubError error ) {
    this.error = error;
  }

  public static class SubError {
    private String msg;
    private Integer code;

    public String getMsg () {
      return msg;
    }

    public void setMsg ( String msg ) {
      this.msg = msg;
    }

    public Integer getCode () {
      return code;
    }

    public void setCode ( Integer code ) {
      this.code = code;
    }

    @Override
    public String toString () {
      return "Error{" +
          "msg='" + msg + '\'' +
          ", code=" + code +
          '}';
    }
  }
}
