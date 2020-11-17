package de.debuglevel.book2graph.graph

interface NodeInformationRetriever<T> {
    fun getColor(node: T): Color
    fun getShape(node: T): Shape
    fun getTooltip(node: T): String
    fun getPrecedingVertices(vertex: Vertex<T>, allVertices: List<Vertex<T>>): List<Vertex<T>>
    fun getSucceedingVertices(vertex: Vertex<T>, allVertices: List<Vertex<T>>): List<Vertex<T>>
}
