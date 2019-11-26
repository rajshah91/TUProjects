package com.oppscience.sgevt.extraction;

import java.awt.List;
import java.util.ArrayList;
import java.util.Arrays;

public class AppConstants {
	

	public final static ArrayList<String> SHEETS_TO_SKIP = new ArrayList<String>() {{
        add("Formules");
        add("Documentation");
        add("Pr�sentation");
        add("Modifications_territoriales");
        add("Historique des mises � jour");
    }};
    
    public final static ArrayList<String> STARTING_FIELDNAMES = new ArrayList<String>() {{
        add("CODGEO");
        add("VAR_ID");
        add("RR17");
    }};
}