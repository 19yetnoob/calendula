/*
 *    Calendula - An assistant for personal medication management.
 *    Copyright (C) 2016 CITIUS - USC
 *
 *    Calendula is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this software.  If not, see <http://www.gnu.org/licenses/>.
 */

package es.usc.citius.servando.calendula.drugdb;

import android.content.Context;
import android.text.TextUtils;

import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

import es.usc.citius.servando.calendula.database.DB;
import es.usc.citius.servando.calendula.persistence.Prescription;
import es.usc.citius.servando.calendula.persistence.Presentation;

/**
 * Created by joseangel.pineiro on 9/8/15.
 */
public class USPrescriptionDBMgr extends PrescriptionDBMgr {

    @Override
    public String getProspectURL(Prescription p) {
        return "http://www.accessdata.fda.gov/spl/data/#ID#/#ID#.xml".replaceAll("#ID#", p.pid);
    }

    @Override
    public Presentation expected(Prescription p) {
        String name = p.name;
        String content = p.content;
        return expected(name, content);
    }

    @Override
    public Presentation expected(String name, String content) {

        String n = name.toLowerCase() + " " + content.toLowerCase();
        if (n.contains("tablet")) {
            return Presentation.PILLS;
        } else if (n.contains("capsule")) {
            return Presentation.CAPSULES;
        } else if (n.contains("inhale")) {
            return Presentation.INHALER;
        } else if (n.contains("injection")) {
            return Presentation.INJECTIONS;
        } else if (n.contains("drops")) {
            return Presentation.DROPS;
        } else if (n.contains("suspension")) {
            return Presentation.EFFERVESCENT;
        } else if (n.contains("cream") || n.contains("gel") || n.contains("powder") || n.contains("paste")) {
            return Presentation.POMADE;
        } else if (n.contains("spray")) {
            return Presentation.SPRAY;
        } else if (!n.contains("liquid")) {
            return Presentation.SYRUP;
        }

        return null;
    }

    @Override
    public String shortName(Prescription p) {
        int max = p.name.length();
        return p.name.substring(0, Math.min(20,max));
    }

    @Override
    public void setup(final Context ctx, final String downloadPath, final SetupProgressListener l) throws Exception {

        final ConnectionSource connection = DB.helper().getConnectionSource();

        TransactionManager.callInTransaction(connection, new Callable<Object>() {
            @Override
            public Object call() throws Exception {

                BufferedReader br;
                String line;
                int progressUpdateBy;
                int lines = 0;
                int i = 0;

                br = new BufferedReader(new InputStreamReader(new FileInputStream(downloadPath)));
                // count file lines (for progress updating)
                while (br.readLine() != null) { lines++;} br.close();
                progressUpdateBy = lines/20;
                updateProgress(l, 0);

                br = new BufferedReader(new InputStreamReader(new FileInputStream(downloadPath)));

                while ((line = br.readLine()) != null) {
                    if (l != null && i % progressUpdateBy == 0) {
                        int progress = (int) (((float) i / lines) * 100);
                        l.onProgressUpdate(progress);
                    }
                    // exec line content as raw sql
                    Prescription prescription = fromCsv(line,"\\|");
                    DB.prescriptions().save(prescription);
                    i++;
                }
                br.close();
                return null;
            }
        });
    }

    private void updateProgress(SetupProgressListener l, int progress){
        if(l!=null) l.onProgressUpdate(progress);
    }

    private Prescription fromCsv(String csvLine, String separator) {

        String[] values = csvLine.split(separator);

        if (values.length != 3) {
            throw new RuntimeException("Invalid CSV. Input string must contain exactly 3 members. " + csvLine);
        }

        if(TextUtils.isEmpty(values[1])){
            return null;
        }

        Prescription p = new Prescription();
        p.cn = values[0];
        p.pid = values[0];
        p.name = values[1];
        p.dose = "0";
        p.content = values[2];
        p.generic = false;
        p.affectsDriving = false;
        p.hasProspect = false;
        p.packagingUnits = 0f;

        return p;
    }

}
