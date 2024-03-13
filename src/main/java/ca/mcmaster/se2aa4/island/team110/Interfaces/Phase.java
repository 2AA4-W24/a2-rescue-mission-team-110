package ca.mcmaster.se2aa4.island.team110.Interfaces;

public interface Phase {
  boolean reachedEnd();

  String getNextDecision();

  Phase getNextPhase();

  boolean isFinal();
}
