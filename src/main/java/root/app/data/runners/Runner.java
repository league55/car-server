package root.app.data.runners;

/**
 * diff methods to run app
 */
public interface Runner {
    void startCapturing();

    void stopCapturing();

    boolean isRunning();
}
