package com.game.walkingpixels.model;

import android.content.Context;
import android.renderscript.Matrix4f;

import com.game.walkingpixels.openGL.Texture;
import com.game.walkingpixels.openGL.vertices.WorldVertex;
import com.game.walkingpixels.util.vector.MatrixAdapter;
import com.game.walkingpixels.util.vector.Vector3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Model3D {

    private final ArrayList<String> listFaceIndices = new ArrayList<>();
    private float[][] positions;
    private float[][] texturePositions;
    private float[][] normals;

    private final int textureSlot;

    public Model3D(Context context, String path, int textureSlot){
        this.textureSlot = textureSlot;
        try {
            readData(context, path + ".obj");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public WorldVertex[] getVertices(Vector3 offset){
        return getVertices(offset, new Vector3());
    }

    public WorldVertex[] getVertices(Vector3 offset, Vector3 rotation){
        return getVertices(offset, rotation, new Vector3(1));
    }

    public WorldVertex[] getVertices(Vector3 offset, Vector3 rotation, Vector3 scale){
        WorldVertex[] vertices = new WorldVertex[listFaceIndices.size()];
        for (int i = 0; i < listFaceIndices.size(); i++) {
            String[] parts = listFaceIndices.get(i).split("/");
            int positionNumber = Integer.parseInt(parts[0]) - 1;
            int texPositionNumber = Integer.parseInt(parts[1]) - 1;
            int normalsNumber = Integer.parseInt(parts[2]) - 1;

            Vector3 position = new Vector3(
                    positions[positionNumber][0],
                    positions[positionNumber][1],
                    positions[positionNumber][2]
            );
            Vector3 normal = new Vector3(
                    normals[normalsNumber][0],
                    normals[normalsNumber][1],
                    normals[normalsNumber][2]
            );

            //Rotation
            Matrix4f rotationMatrix = new Matrix4f();
            rotationMatrix.loadIdentity();
            rotationMatrix.scale(scale.x, scale.y, scale.z);
            rotationMatrix.rotate(rotation.x, 1.0f, 0.0f, 0.0f);
            rotationMatrix.rotate(rotation.y, 0.0f, 1.0f, 0.0f);
            rotationMatrix.rotate(rotation.z, 0.0f, 0.0f, 1.0f);
            position = MatrixAdapter.multiply(rotationMatrix, position);
            normal = MatrixAdapter.multiply(rotationMatrix, normal);

            position = position.add(offset);

            vertices[i] = new WorldVertex(
                    new float[]{position.x, position.y, position.z},
                    new float[]{texturePositions[texPositionNumber][0], texturePositions[texPositionNumber][1]},
                    new float[]{normal.x, normal.y, normal.z},
                    new float[]{0.0f, 0.0f, 0.0f, 0.0f},
                    textureSlot
            );
        }

        return vertices;
    }

    public void readData(Context context, String path) throws IOException {
        InputStream is = context.getAssets().open(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        ArrayList<Float> listPositions = new ArrayList<>();
        ArrayList<Float> listTexPositions = new ArrayList<>();
        ArrayList<Float> listNormals = new ArrayList<>();
        String line;
        while((line = br.readLine()) != null){
            if(line.contains("v ")){
                String[] parts = line.split(" ");
                listPositions.add(Float.parseFloat(parts[1]));
                listPositions.add(Float.parseFloat(parts[2]));
                listPositions.add(Float.parseFloat(parts[3]));
            }
            else if(line.contains("vt ")){
                String[] parts = line.split(" ");
                listTexPositions.add(Float.parseFloat(parts[1]));
                listTexPositions.add(Float.parseFloat(parts[2]));
            }
            else if(line.contains("vn ")){
                String[] parts = line.split(" ");
                listNormals.add(Float.parseFloat(parts[1]));
                listNormals.add(Float.parseFloat(parts[2]));
                listNormals.add(Float.parseFloat(parts[3]));
            }
            else if(line.contains("f ")){
                String[] parts = line.split(" ");
                listFaceIndices.add(parts[1]);
                listFaceIndices.add(parts[2]);
                listFaceIndices.add(parts[3]);
            }
        }

        convertToVertexPositions(listPositions);
        convertToVertexTexPositions(listTexPositions);
        convertToVertexNormals(listNormals);
    }

    public void convertToVertexPositions(ArrayList<Float> listPositions){
        positions = new float[listPositions.size() / 3][3];
        for (int i = 0; i < listPositions.size() / 3; i++) {
            positions[i][0] = listPositions.get(i * 3);
            positions[i][1] = listPositions.get((i * 3) + 1);
            positions[i][2] = listPositions.get((i * 3) + 2);
        }
    }

    public void convertToVertexTexPositions(ArrayList<Float> listTexPositions){
        texturePositions = new float[listTexPositions.size() / 2][2];
        for (int i = 0; i < listTexPositions.size() / 2; i++) {
            texturePositions[i][0] = listTexPositions.get(i * 2);
            texturePositions[i][1] = listTexPositions.get((i * 2) + 1);
        }
    }

    public void convertToVertexNormals(ArrayList<Float> listNormals){
        normals = new float[listNormals.size() / 3][3];
        for (int i = 0; i < listNormals.size() / 3; i++) {
            normals[i][0] = listNormals.get(i * 3);
            normals[i][1] = listNormals.get((i * 3) + 1);
            normals[i][2] = listNormals.get((i * 3) + 2);
        }
    }
}
