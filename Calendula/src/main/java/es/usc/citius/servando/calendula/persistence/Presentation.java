package es.usc.citius.servando.calendula.persistence;

import android.content.res.Resources;

import es.usc.citius.servando.calendula.R;

/**
 * Created by joseangel.pineiro on 12/10/13.
 */
public enum Presentation {

    INJECTIONS(R.drawable.icp_injection, R.string.injections, R.string.injections_units),
    CAPSULES(R.drawable.icp_capsule, R.string.capsules, R.string.capsules_units),
    EFFERVESCENT(R.drawable.icp_effervescent, R.string.effervescent, R.string.effervescent_units),
    PILLS(R.drawable.icp_pill, R.string.pills, R.string.pills_units),
    SYRUP(R.drawable.icp_syrup, R.string.syrup, R.string.syrup_units),
    DROPS(R.drawable.icp_drop, R.string.drops, R.string.drops_units),
    POMADE(R.drawable.ic_ppomade, R.string.pomade, R.string.pomade_units),
    INHALER(R.drawable.icp_inhaler, R.string.inhaler, R.string.inhaler_units),
    SPRAY(R.drawable.icp_nasalspray, R.string.spray, R.string.spray_units),


    UNKNOWN(R.drawable.ic_presentation_6, R.string.unknown, R.string.unknown_units);

    private int drawable = R.drawable.icp_injection;
    private int nameString = R.string.unknown;
    private int unitsString = R.string.unknown_units;

    Presentation(int drawable, int nameString, int unitsString) {
        this.drawable = drawable;
        this.nameString = nameString;
        this.unitsString = unitsString;
    }

    public int getDrawable() {
        return drawable;
    }

    public String getName(Resources r) {
        return r.getString(nameString);
    }

    public String units(Resources r) {
        return r.getString(unitsString);
    }

}
