// src/main/java/org/platform/report/SimpleHtmlReport.java
package org.example;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class SimpleHtmlReport implements AutoCloseable {
    private final String testName;
    private final BufferedWriter out;
    private boolean closed = false;

    /** reportPath is available after construction */
    private final String reportPath;

    public SimpleHtmlReport(String reportsDir, String testName) {
        try {

            // ✅ 1. Determine where to write (GitHub Actions or local)
            Path root = Paths.get(System.getenv().getOrDefault("GITHUB_WORKSPACE", System.getProperty("user.dir")));
            Path outDir = root.resolve("reports");
            Files.createDirectories(outDir);

            this.testName = (testName == null || testName.isBlank()) ? "Test" : testName;
            Path dir = Paths.get((reportsDir == null || reportsDir.isBlank()) ? "reports" : reportsDir);
            Files.createDirectories(dir);

            String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
            this.reportPath = dir.resolve(testName +" - "+ ts + ".html").toString();

            this.out = Files.newBufferedWriter(Paths.get(reportPath), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            writeHeader();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to init SimpleHtmlReport", e);
        }
    }

    public String getReportPath() { return reportPath; }

    /* ---------------- Logging API ---------------- */

    public void pass(String step)  { line("PASS", step, "#16a34a"); } // green
    public void fail(String step)  { line("FAIL", step, "#dc2626"); } // red
    public void info(String step)  { line("INFO", step, "#2563eb"); } // blue

    /** passOrFailed("Step name", true/false) */
    public void passOrFailed(String step, boolean isPass) { if (isPass) pass(step); else fail(step); }

    /* ---------------- Internals ---------------- */

    private void line(String level, String msg, String color) {
        if (closed) return;
        try {
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            out.write(
                    "<tr>" +
                            "<td class='time'>" + time + "</td>" +
                            "<td><span class='badge' style='background:" + color + "'>" + level + "</span></td>" +
                            "<td>" + escape(msg) + "</td>" +
                            "</tr>\n"
            );
            out.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void writeHeader() throws IOException {
        out.write("<!doctype html><html lang='en'><head><meta charset='utf-8'/>");
        out.write("<meta name='viewport' content='width=device-width,initial-scale=1'/>");
        out.write("<title>" + escape(testName) + " - Report</title>");
        out.write("<style>");
        out.write("body{font-family:ui-sans-serif,system-ui,-apple-system,Segoe UI,Roboto,Arial,sans-serif;background:#0b1220;color:#e5e7eb;margin:0;}");
        out.write(".wrap{max-width:960px;margin:32px auto;padding:0 16px;}");
        out.write(".card{background:#0f172a;border:1px solid #1f2937;border-radius:12px;overflow:hidden;}");
        out.write(".head{padding:18px 20px;border-bottom:1px solid #1f2937;display:flex;justify-content:space-between;align-items:center}");
        out.write(".title{font-weight:600;font-size:18px}");
        out.write(".stamp{opacity:.7;font-size:12px}");
        out.write("table{width:100%;border-collapse:collapse}");
        out.write("th,td{padding:12px 16px;text-align:left;border-bottom:1px solid #1f2937;vertical-align:top}");
        out.write("th{background:#111827;}");
        out.write(".badge{display:inline-block;padding:2px 8px;border-radius:999px;color:#fff;font-size:12px;font-weight:600}");
        out.write(".time{white-space:nowrap;color:#9ca3af}");
        out.write("</style></head><body><div class='wrap'><div class='card'>");
        out.write("<div class='head'><div class='title'>" + escape(testName) + "</div>");
        out.write("<div class='stamp'>Started: " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "</div></div>");
        out.write("<table><thead><tr><th style='width:120px'>Time</th><th style='width:90px'>Level</th><th>Message</th></tr></thead><tbody>\n");
    }

    private void writeFooter() {
        if (closed) return;
        try {
            out.write("</tbody></table></div></div></body></html>");
            out.flush();
        } catch (IOException ignored) {
        }
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    @Override
    public void close() {
        if (closed) return;
        writeFooter();
        try { out.close(); } catch (IOException ignored) {}
        closed = true;
    }
}
