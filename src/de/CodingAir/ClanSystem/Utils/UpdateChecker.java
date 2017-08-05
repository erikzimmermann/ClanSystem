package de.CodingAir.ClanSystem.Utils;

import de.CodingAir.ClanSystem.ClanSystem;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class UpdateChecker {
    private URL url;
    private String version = null;
    private String download = null;

    public UpdateChecker(String url) {
        try {
            this.url = new URL(url);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
    }

    public boolean needsUpdate() {
        if (this.url == null) return false;

        this.version = null;
        this.download = null;

        try {
            URLConnection con = this.url.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setConnectTimeout(5000);
            con.connect();

            BufferedReader input = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String line;
            while ((line = input.readLine()) != null) {

                if (this.version != null && this.download != null) break;

                if (line.contains("<td class=\"version\">") && this.version == null) {
                    this.version = line.split(">")[1].split("<")[0];
                } else if (line.contains("<td class=\"dataOptions download\">") && download == null) {
                    this.download = "https://www.spigotmc.org/" + line.split("href=\"")[1].split("\"")[0];
                }
            }

            if (this.version == null) return false;
        } catch (Exception ex) {
            return false;
        }

        boolean needsUpdate = false;
        double currentVersion = Double.parseDouble(ClanSystem.getInstance().getDescription().getVersion().replace(".", "").replace("|", "."));
        double remoteVersion = Double.parseDouble(this.version.replace(".", "").replace("|", "."));

        if (remoteVersion > currentVersion) needsUpdate = true;

        return needsUpdate;
    }

    public String getDownload() {
        return download;
    }

    public String getVersion() {
        return version;
    }

    public URL getUrl() {
        return url;
    }
}
