package structures;

import graphs.Edge;
import graphs.IGraph;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * This edge-weighted digraph implementation makes use of multiple data structures.
 * @param <V>
 * @author Tobie Sagun
 * @version 1.02
 */
public class DirectedGraph<V> implements IGraph<V>
{

    private int[][] adjMatrix;
    private int edges = 0;
    private Stack<Integer> stack = new Stack<>();
    private Bijection<V, Integer> table = new Bijection<>();
    private static final double GROWTH_FACTOR = 1.5;

    /**
     * Default constructor that creates an int[10][10] matrix with all indices containing "missing edges"
     */
    public DirectedGraph()
    {
        adjMatrix = new int[10][10];
        initializeMatrix();
        stack.push(0);
    }

    //helper method for setting all edges to -1
    private void initializeMatrix()
    {
        for (int[] matrix : adjMatrix)
        {
            Arrays.fill(matrix, -1);
        }
    }


    @Override
    public boolean addVertex(V vertex)
    {
        if (!containsVertex(vertex)) //if vertex is not in the graph
        {
            //start updating data structures
            if (table.keySet().size() > adjMatrix.length / 2) //if array is full, resize
            {
                resize();
            }
            int newIndex = stack.pop();
            if (stack.isEmpty())
            {
                stack.push(newIndex + 1);
            }
            table.add(vertex, newIndex);

            return true;
        }
        return false;
    }

    //helper method for resizing matrix
    private void resize()
    {
        int oldLength = adjMatrix.length;
        int newLength = (int) (adjMatrix.length * GROWTH_FACTOR);
        adjMatrix = Arrays.copyOf(adjMatrix, newLength);
        for (int i = 0; i < oldLength; i++)
        {
            adjMatrix[i] = Arrays.copyOf(adjMatrix[i], newLength);
            Arrays.fill(adjMatrix[i], oldLength - 1, newLength, -1);
        }
        for (int i = oldLength; i < newLength; i++)
        {
            adjMatrix[i] = new int[newLength];
            Arrays.fill(adjMatrix[i], -1);
        }
    }


    @Override
    public boolean addEdge(V source, V destination, int weight)
    {
        if (vertices().contains(source) && vertices().contains(destination)) //if vertices are in graph
        {
            int sourceIndex = table.getValue(source), destIndex = table.getValue(destination);

            if (adjMatrix[sourceIndex][destIndex] == -1) //if edge is missing, create edge
            {
                adjMatrix[sourceIndex][destIndex] = weight;
                edges++;
                return true;
            }
        }
        return false;
    }

    @Override
    public int vertexSize()
    {
        return table.keySet().size();
    }

    @Override
    public int edgeSize()
    {
        return edges;
    }

    @Override
    public boolean containsVertex(V vertex)
    {
        return table.containsKey(vertex);
    }

    @Override
    public boolean containsEdge(V source, V destination)
    {
        if (containsVertex(source) && containsVertex(destination))
        {
            return adjMatrix[table.getValue(source)][table.getValue(destination)] > -1;
        }
        return false;
    }

    @Override
    public int edgeWeight(V source, V destination)
    {
        return adjMatrix[table.getValue(source)][table.getValue(destination)];
    }

    @Override
    public Set<V> vertices()
    {
        return new HashSet<>(table.keySet());
    }

    @Override
    public Set<Edge<V>> edges()
    {
        HashSet<Edge<V>> allEdges = new HashSet<>();
        for (int i = 0; i < adjMatrix.length; i++) //iterate through matrix
        {
            for (int j = 0; j < adjMatrix.length; j++)
            {
                if (adjMatrix[i][j] > -1) //if edge is found
                {
                    V source = table.getKey(i), destination = table.getKey(j);
                    allEdges.add(new Edge<>(source, destination, adjMatrix[i][j]));
                }
            }
        }
        return allEdges;
    }

    @Override
    public boolean removeVertex(V vertex)
    {
        if (containsVertex(vertex))
        {
            int vIndex = table.getValue(vertex);

            //start updating data structures!
            Arrays.fill(adjMatrix[vIndex], -1); //updating adjMatrix
            for (int i = 0; i < adjMatrix.length; i++)
            {
                adjMatrix[i][vIndex] = -1;
            }
            table.removeKey(vertex); //update table
            stack.push(vIndex); //push index on stack for another vertex
            return true;
        }
        return false;
    }

    @Override
    public boolean removeEdge(V source, V destination)
    {
        Integer sourceIndex = table.getValue(source), destIndex = table.getValue(destination);
        //if vertices and edge exist in graph
        if (sourceIndex != null && destIndex != null && adjMatrix[sourceIndex][destIndex] > -1)
        {
            adjMatrix[sourceIndex][destIndex] = -1;
            edges--;
            return true;
        }
        return false;
    }

    @Override
    public void clear()
    {
        initializeMatrix();
        stack.clear();
        table.clear();
        edges = 0;
    }
}
