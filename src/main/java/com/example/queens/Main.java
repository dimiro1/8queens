package com.example.queens;

import io.jenetics.Genotype;
import io.jenetics.IntegerChromosome;
import io.jenetics.IntegerGene;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.engine.Limits;
import io.jenetics.util.Factory;
import io.jenetics.util.IntRange;

public class Main {
    private static final int SIZE = 8;

    public static void main(String[] args) {
        // Problem definition
        Factory<Genotype<IntegerGene>> gtf =
                Genotype.of(IntegerChromosome.of(IntRange.of(0, SIZE - 1)), SIZE);

        // Build the world
        Engine<IntegerGene, Integer> engine = Engine
                .builder(Main::fitness, gtf)
                .populationSize(50)
                .minimizing()
                .build();

        // Collect stats
        EvolutionStatistics<Integer, ?> statistics = EvolutionStatistics.ofNumber();
        // Execute the simulation
        Genotype<IntegerGene> result = engine.stream()
                .limit(Limits.byFitnessThreshold(1))
                .peek(statistics)
                .collect(EvolutionResult.toBestGenotype());

        printBoard(result);
        printStatistics(statistics);
    }

    private static void printStatistics(EvolutionStatistics<Integer, ?> statistics) {
        System.out.println(statistics);
    }

    private static void printBoard(Genotype<IntegerGene> result) {
        for (int i = 0; i < SIZE; i++) {
            System.out.println("---------------------------------");
            for (int j = 0; j < SIZE + 1; j++) {
                if (result.getChromosome(i).getGene().intValue() == j) {
                    System.out.print("| " + "♛" + " ");
                } else {
                    System.out.print("| " + " " + " ");
                }
            }
            System.out.println();
        }
        System.out.println("---------------------------------");
        System.out.println(result);
    }

    // Calculate the number of collisions
    private static Integer fitness(Genotype<IntegerGene> gt) {
        int collisions = 0;
        int dx;
        int dy;

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (i == j) {
                    continue;
                }

                // horizontal
                if (gt.getChromosome(i).getGene().intValue() == gt.getChromosome(j).getGene().intValue()) {
                    collisions++;
                }

                // diagonal
                dx = Math.abs(i - j);
                dy = Math.abs(gt.getChromosome(i).getGene().intValue() - gt.getChromosome(j).getGene().intValue());
                if (dx == dy) {
                    collisions++;
                }
            }
        }

        return collisions;
    }
}
