package net.deechael.dodo.types;

public enum ArticleObjectType {
    THREAD(1),
    THREAD_COMMENT(2),
    THREAD_COMMENT_REPLY(3);

    private final int code;

    ArticleObjectType(int status) {
        this.code = status;
    }

    public int getCode() {
        return code;
    }

    public static ArticleObjectType of(int code) {
       switch (code){
           case 1:
               return THREAD;
           case 2:
               return THREAD_COMMENT;
           default:
               return THREAD_COMMENT_REPLY;
       }
    }

}
