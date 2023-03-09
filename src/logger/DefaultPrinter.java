package logger;

/**
 * 默认的日志打印器
 *
 * @author 大定府羡民（QQ：1032694760）
 */
class DefaultPrinter implements IPrinter {

    @Override
    public void print(String msg) {
        System.out.println(msg);
    }

}
