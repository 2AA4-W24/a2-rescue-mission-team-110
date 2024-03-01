package ca.mcmaster.se2aa4.island.team110.Models;

import java.util.Map;

import ca.mcmaster.se2aa4.island.team110.Models.Mesh;

import java.util.List;

public class Information {
    private Map<Integer, Map<String, Object>> edgeProps;
    private Map<Integer, Map<String, Object>> vertexProps;
    private Map<Integer, Map<String, Object>> faceProps;
    private Mesh mesh;

    public void setEdgeProps(Map<Integer, Map<String, Object>> edgeProps) {
        this.edgeProps = edgeProps;
    }

    public void setVertexProps(Map<Integer, Map<String, Object>> vertexProps) {
        this.vertexProps = vertexProps;
    }

    public void setFaceProps(Map<Integer, Map<String, Object>> faceProps) {
        this.faceProps = faceProps;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    public Map<Integer, Map<String,Object>> getEdgeProps() {
        return edgeProps;
    }

    public Map<Integer,Map<String, Object>> getVertexProps() {
        return vertexProps;
    }

    public Map<Integer, Map<String, Object>> getFaceProps() {
        return faceProps;
    }

    public Mesh getMesh() {
        return mesh;
    }
}