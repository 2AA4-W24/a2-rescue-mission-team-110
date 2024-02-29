package ca.mcmaster.se2aa4.island.team110.Interfaces;

import ca.mcmaster.se2aa4.island.team110.Models.Information;

public interface Translator {
    Information translateJson(String jsonContent);
}
