package com.game.walkingpixels.openGL;

import com.game.walkingpixels.openGL.buffer.IndexBuffer;
import com.game.walkingpixels.openGL.buffer.VertexBuffer;
import com.game.walkingpixels.openGL.buffer.VertexBufferLayout;
import com.game.walkingpixels.openGL.vertices.IVertex;

import java.util.ArrayList;

import static android.opengl.GLES31.*;


public class Batch {

    public static class BatchPart{
        public final String name;
        public int offset;
        public IVertex[] vertices;
        public BatchPart(String name, int offset,  IVertex[] vertices){
            this.name = name;
            this.offset = offset;
            this.vertices = vertices;
        }
    }

    private final ArrayList<BatchPart> parts = new ArrayList<>();


    private final VertexBuffer vb;
    private final IndexBuffer ib;
    private final ArrayList<Texture> textures = new ArrayList<>();

    private int lastVertexPosition = 0;

    public Batch(int shaderID, int maxQuadCount, int vertexSize, VertexBufferLayout[] layouts){
        vb = new VertexBuffer(shaderID, maxQuadCount * 4, vertexSize, layouts);

        short[] indices = new short[maxQuadCount * 6];

        for(int i = 0; i < maxQuadCount*2; i++){
            if(i % 2 == 0){
                indices[i * 3] = (short)(i * 2);
                indices[i * 3 + 1] = (short)(i * 2 + 1);
                indices[i * 3 + 2] = (short)(i * 2 + 2);
            }
            else {
                indices[i * 3] = (short)(i * 2);
                indices[i * 3 + 1] = (short)(i * 2 + 1);
                indices[i * 3 + 2] = (short)(i * 2 - 1);
            }
        }

        ib = new IndexBuffer(indices);
    }

    public void addVertices(String name, IVertex[] vertices){
        if(parts.size() == 0)
            parts.add(new BatchPart(name, 0, vertices));
        else
            parts.add(new BatchPart(name, parts.get(parts.size() - 1).offset + parts.get(parts.size() - 1).vertices.length, vertices));

        lastVertexPosition += (vertices.length / 4) * 6;

        vb.fillPartBuffer(vertices, parts.get(parts.size() - 1).offset);
    }

    public void updateVertices(String name, IVertex[] vertices){
        int partNumber = -1;
        for (int i = 0; i < parts.size(); i++) {
            if(parts.get(i).name.equals(name)){
                partNumber = i;
                break;
            }
        }

        if(partNumber == -1){
            addVertices(name, vertices);
            return;
        }

        int oldSize = parts.get(partNumber).vertices.length;

        parts.get(partNumber).vertices = vertices;
        vb.fillPartBuffer(vertices, parts.get(partNumber).offset);

        if(vertices.length != oldSize){

            lastVertexPosition += ((vertices.length - oldSize) / 4) * 6;

            for (int i = partNumber + 1; i < parts.size(); i++){
                parts.get(i).offset = parts.get(i - 1).offset + parts.get(i - 1).vertices.length;
                vb.fillPartBuffer(parts.get(i).vertices, parts.get(i).offset);
            }
        }
    }

    public void removePart(String name){
        int partNumber = -1;
        for (int i = 0; i < parts.size(); i++) {
            if(parts.get(i).name.equals(name)){
                partNumber = i;
                break;
            }
        }

        if(partNumber == -1)
            return;

        int oldSize = parts.get(partNumber).vertices.length;

        lastVertexPosition -= ((oldSize) / 4) * 6;
        for (int i = partNumber + 1; i < parts.size(); i++){
            parts.get(i).offset = parts.get(i - 1).offset + parts.get(i - 1).vertices.length;
            vb.fillPartBuffer(parts.get(i).vertices, parts.get(i).offset);
        }

        parts.remove(partNumber);
    }

    public void addTexture(Texture texture){
        textures.add(texture);
    }

    public void bind(){
        vb.bind();
        ib.bind();
        for (Texture texture : textures)
            texture.bind();
    }

    public void bind(int shaderID){
        vb.bind(shaderID);
        ib.bind();
        for (Texture texture : textures)
            texture.bind();
    }

    public void unbind(){
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }


    public void draw(){
        glDrawElements(GL_TRIANGLES, lastVertexPosition, GL_UNSIGNED_SHORT, 0);
    }

    public void drawPart(String name){
        int partNumber = -1;
        for (int i = 0; i < parts.size(); i++) {
            if(parts.get(i).name.equals(name)){
                partNumber = i;
                break;
            }
        }

        if(partNumber == -1)
            return;

        int lastVertexPosition = (parts.get(partNumber).offset + parts.get(partNumber).vertices.length / 4) * 6;
        int offset = (parts.get(partNumber).offset / 4) * 6;
        glDrawElements(GL_TRIANGLES, lastVertexPosition, GL_UNSIGNED_SHORT, offset);
    }

    public ArrayList<Texture> getTextures() {return textures;}

    public  ArrayList<BatchPart> getParts() {return parts;}
}
