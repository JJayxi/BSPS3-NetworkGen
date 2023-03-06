# ReadMe

**NetworkGen** is a project made part of the Bachelor Semester Project course in University of Luxembourg. The project uses both manually programmed genetic algorithms and a swarm-based slime mold simulation to construct a network on top of a population density map represented as a black and white image of size 600-600. This java program contains a user interface which allows loading and changing the parameters of the genetic algorithm and the swarm simulation. 

## Pipeline
The image passes through 2 steps before the network in generated. The genetic algorithm step which positions the main points of the network, and the swarm simulation which connects these points in an optimal manner.

## Genetic Algorithm Step
The first step uses genetic algorithm to optimise the position of a specified amount of points, in a way that maximises the distance between the points and the population within each reach of the points. The genetic algorithm was implemented manually since this project is of educational purpose only.

## Swarm Simulation
The second step uses the position selected by the genetic algorithm  to create an environment for agents to live in. These points are considered as food sources, or more abstractly, points that the agent thrive to go towards. The agents also deposit pheromones to change the behaviour of the other agents. With the help of some parameters, this simulates the behaviour of slime molds, a mushroom know to connect food sources in an optimal network.
