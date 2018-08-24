//
// Translated by CS2J (http://www.cs2j.com): 24.08.2018 23:51:08
//

package Graphviz4Net.WPF.Example;

import Graphviz4Net.WPF.Example.Person;

public class MainWindowViewModel   
{
    public MainWindowViewModel() throws Exception {
        Graph<Person> graph = new Graph<Person>();
        Person a = new Person();
        Person b = new Person();
        Person c = new Person();
        graph.AddVertex(a);
        graph.AddVertex(b);
        graph.AddVertex(c);
        graph.AddEdge(new Edge<Person>(c, a));
        this.setGraph(graph);
    }

    private Graph<Person> __Graph = new Graph<Person>();
    public Graph<Person> getGraph() {
        return __Graph;
    }

    public void setGraph(Graph<Person> value) {
        __Graph = value;
    }

}


