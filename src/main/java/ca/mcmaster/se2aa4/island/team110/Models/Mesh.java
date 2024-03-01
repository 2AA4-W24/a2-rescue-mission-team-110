package ca.mcmaster.se2aa4.island.team110.Models;

import java.util.List;

public class Mesh {
    private int size;
    private List<List<Double>> vertices;
    private List<List<Integer>> edges;
    private List<Face> faces;

}

class Face {
    private List<Integer> neighbors;
    private int center;
    private List<Integer> edges;

}