# Travelling Salesman Problem (TSP) – AMOD Project

## Overview

This project was developed for the course **Algorithms and Models for Discrete Optimization**.

The goal is to study and compare exact and heuristic approaches for solving the **Travelling Salesman Problem (TSP)**.

The project implements two exact mathematical formulations, DFJ and MTZ, solved using Gurobi Optimizer, and a heuristic approach based on Nearest Neighbor combined with 2-opt.

## Implemented Approaches

### Exact Methods

- **DFJ (Dantzig–Fulkerson–Johnson):** subtour elimination constraints are dynamically added using lazy constraints and a Gurobi callback.
- **MTZ (Miller–Tucker–Zemlin):** subtour elimination through auxiliary ordering variables.

### Heuristic Method

- **Nearest Neighbor:** constructs an initial tour by repeatedly selecting the nearest unvisited city.
- **2-opt:** improves the initial tour by replacing pairs of edges when the resulting tour has a lower cost.

## Technologies

- Java
- Gurobi Optimizer 13.0.3
- IntelliJ IDEA
- TSPLIB benchmark instances

## Benchmark Instances

| Instance | Number of cities | Known optimum |
|---|---:|---:|
| eil51 | 51 | 426 |
| st70 | 70 | 675 |
| kroA100 | 100 | 21282 |
| ch150 | 150 | 6528 |
| d198 | 198 | 15780 |

## Experimental Methodology

- Exact formulations: 10 independent runs per instance.
- Solver time limit: 300 seconds per run.
- Number of solver threads: 1.
- Heuristic: 1,000 warm-up runs and 5,000 measured runs.
- Heuristic measurements are divided into 50 batches.
- File reading and distance matrix construction are excluded from algorithm execution times.

The experiments compare solution quality, execution time, lower bounds, MIP gaps and the ability to prove optimality.

## Results

The experimental results show that:

- DFJ proves optimality in all runs up to `ch150`.
- On `d198`, DFJ finds the known optimal value but does not prove optimality within the time limit.
- MTZ proves optimality on `eil51` and `st70`, but has greater difficulty on larger instances.
- Nearest Neighbor combined with 2-opt produces feasible solutions very quickly, improving the initial tour on all five instances.

Detailed numerical results are available in the `results/` directory.

## Project Structure

```text
AMOD-TSP-Project/
├── src/
│   └── amod/
│       ├── dfj/
│       ├── heuristic/
│       ├── mtz/
│       ├── results/
│       └── test/
├── instances/
├── results/
├── README.md
└── .gitignore
```

## Requirements

- Java Development Kit (JDK)
- Gurobi Optimizer 13.0.3 with a valid license
- Gurobi Java API configured in the project

Gurobi must be installed and configured separately. Its proprietary libraries and license files are not included in this repository.

## How to Run

1. Clone this repository.
2. Install and configure Gurobi Optimizer.
3. Configure the Gurobi Java library in your IDE.
4. Open the project in IntelliJ IDEA.
5. Run `amod.Main` or the relevant experimental classes.

## Academic Context

Developed as a university project for **Algorithms and Models for Discrete Optimization (AMOD)**.

The project focuses on the formulation, implementation and experimental evaluation of exact and heuristic optimization methods for the TSP.