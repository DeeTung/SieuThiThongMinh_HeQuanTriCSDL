package business.service;

import business.sql.rbac.TokenSql;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TokenCleanupService {
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static boolean started = false;

    private TokenCleanupService() {}

    public static synchronized void start() {
        if (started) return;
        started = true;

        scheduler.scheduleAtFixedRate(() -> {
            try {
                int deleted = TokenSql.getInstance().deleteExpiredTokens();
                if (deleted > 0) {
                    System.out.println("TOKEN CLEANUP deleted = " + deleted);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 15, 15, TimeUnit.MINUTES); // mỗi 15 phút
    }

    public static void stop() {
        scheduler.shutdownNow();
    }
}