package landon.core.patchapi.patches.chunkgc;

import org.fusesource.jansi.Ansi;

final class ChunkGCLogger {
    private final String ansi_green = Ansi.ansi().fg(Ansi.Color.GREEN).boldOff().toString();

    private final String ansi_yellow = Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString();

    private final String ansi_red = Ansi.ansi().fg(Ansi.Color.RED).boldOff().toString();

    private final String ansi_reset = Ansi.ansi().a(Ansi.Attribute.RESET).boldOff().toString();

    public void notice(String s, Class<?> c) {
        System.out.println(this.ansi_green + "[ChunkGC (" + c.getSimpleName() + ")] " + s + this.ansi_reset);
    }

    public void debug(String s, Class<?> c) {
        System.out.println(this.ansi_yellow + "(DE) " + this.ansi_reset + "[ChunkGC (" + c.getSimpleName() + ")] " + s + this.ansi_reset);
    }

    public void log(String s, Class<?> c) {
        System.out.println("[ChunkGC (" + c.getSimpleName() + ")] " + s);
    }

    public void warning(String s, Class<?> c) {
        System.out.println(this.ansi_yellow + "[ChunkGC (" + c.getSimpleName() + ")] " + s + this.ansi_reset);
    }

    public void error(String s, Class<?> c) {
        System.out.println(this.ansi_red + "[ChunkGC (" + c.getSimpleName() + ")] " + s + this.ansi_reset);
    }
}
