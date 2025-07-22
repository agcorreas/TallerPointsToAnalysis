# TallerPointsToAnalysis

Un analisis de points-to es un may forward dataflow analysis que permite determinar a que objetos
puede apuntar una variable durante la ejecucion de un programa. En este taller desarrollamos
una version intra-procedural de este algoritmo. Esto significa que vamos a asumir que el programa no
tiene llamadas a funciones.
Para implementar el analisis utilizamos un points-to graph
