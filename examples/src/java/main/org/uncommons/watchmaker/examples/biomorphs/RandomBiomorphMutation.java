//=============================================================================
// Copyright 2006-2010 Daniel W. Dyer
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//=============================================================================
package org.uncommons.watchmaker.examples.biomorphs;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.lang.Math;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

/**
 * Mutation operator for biomorphs.  Mutates each individual gene
 * according to some mutation probability.
 * @author Daniel Dyer
 * Modified by Michael Forrest
 */
public class RandomBiomorphMutation implements EvolutionaryOperator<Biomorph>
{
    private final Probability mutationProbability;
    private final String[] fSubComponents = new String[]{"f", "g", "h", "+", "-"};


    /**
     * @param mutationProbability The probability that a given gene
     * is changed.
     */
    public RandomBiomorphMutation(Probability mutationProbability)
    {
        this.mutationProbability = mutationProbability;
    }


    /**
     * Randomly mutate each selected candidate.
     * @param selectedCandidates {@inheritDoc}
     * @param rng {@inheritDoc}
     * @return {@inheritDoc}
     */
    public List<Biomorph> apply(List<Biomorph> selectedCandidates, Random rng)
    {
        List<Biomorph> mutatedPopulation = new ArrayList<Biomorph>(selectedCandidates.size());
        for (Biomorph biomorph : selectedCandidates)
        {
            mutatedPopulation.add(mutateBiomorph(biomorph, rng));
        }
        return mutatedPopulation;
    }


    /**
     * Mutates a single biomorph.
     * @param biomorph The biomorph to mutate.
     * @param rng The source of randomness to use for mutation.
     * @return A mutated version of the biomorph.
     */
    private Biomorph mutateBiomorph(Biomorph biomorph, Random rng) //given a biomorph, mutates the axiom, rule list and turn angle
    {

        String axiom = mutateAxiom(biomorph.getAxiom());
        String[] ruleList =  mutateRuleList(biomorph.getRuleList());
        int turnAngle = mutateTurnAngle(biomorph.getTurnAngle());

        return new Biomorph(axiom, ruleList, turnAngle);
    }

    public String mutateAxiom(String axiom){
        Random rng = new Random();
        if (mutationProbability.nextEvent(rng)){ //Using the probability provided by BiomorphApplet, semi-randomly decides whether or not to modify the axiom
            int replacementPosition = rng.nextInt(axiom.length()); //randomly decides which character to replace
            if (replacementPosition == 0) {
                if (axiom.length() > 1){
                    axiom = (fSubComponents[rng.nextInt(3)]) + axiom.substring(1, ((axiom.length()) - 1)); //if the replacement position is the first character, set the axiom equal to a new random fsubcomponent plus everything after the first element in the original axiom
                }
                else{
                    axiom = fSubComponents[rng.nextInt(3)]; //if the string is null, add a random character from {f,g,h}
                }
            }
            else if  (replacementPosition == (axiom.length() - 1)) { //if replacement position is the last character
                axiom = axiom.substring(0, (axiom.length() - 2)) + fSubComponents[rng.nextInt(3)]; //set the axiom equal to everything except the last character plus a random fsubcomponent
            }
            else {
                axiom = axiom.substring(0, replacementPosition) + fSubComponents[rng.nextInt(3)] + axiom.substring((replacementPosition + 1), (axiom.length() - 1)); //set the axiom equal to everything before the replacement position + a random fsubcomponent + everything after the replacement position
            }

        }
        return axiom;
    }

    public String[] mutateRuleList(String[] ruleList){
        String[] bComponents = generateBComponents();
        int numRules = Array.getLength(ruleList);
        Random rng = new Random();
        boolean noReplacements = false;
        for (int i = 0; i < numRules; i++){ //walks through the rule list, mutating each rule by deleting several randomly selected components and then adding new ones
            double numBracketSets = 0;
            for (int j = 0; j < ruleList[i].length(); j++){ //walks through the rule, counts how many opening brackets it finds. theoretically there should be an closing bracket for every opening bracket
                if (ruleList[i].charAt(j) == '['){
                    numBracketSets += 1;
                }
            }
            if (numBracketSets >= 3){
            int setsToDelete = (int) Math.round(numBracketSets * .8); //setsToDelete is set equal to 80% of the total number of bracket sets, ensuring that strings do not grow too large.

            for (int x = 0; x < setsToDelete; x++) { //will remove a number of brackets sets from the string, proportional to its size;
                //for each set, the function will pick a random character in the string, begin walking through the string in a random direction and search for brackets sets. once found, the sets will be removed from the string.
                boolean direction = rng.nextBoolean(); //true = left, false = right
                int pointerLocation = rng.nextInt(ruleList[i].length()); //random start location
                if (pointerLocation == 0 || pointerLocation == 1) { //the first two characters of the rule should be ignored
                    pointerLocation = 2;
                }
                int bracket1Location = 0;
                int bracket2Location = 0;
                boolean bracket1Found = false;
                boolean bracket2Found = false;
                int numSearches = 0;
                while (!bracket2Found) { //will exit once a bracket pair is found
                    int maxSearches = 100; //occasionally strings do not have an even number of brackets and get caught in this loop, therefore after 100 fruitless searches the program will exit the loop and make no replacements
                    numSearches++;
                    if (numSearches < maxSearches) {
                        if (bracket1Found && ((pointerLocation == (ruleList[i].length() - 1)) || ((pointerLocation == (0))))) { //if it has found only one half of a bracket set and the point is either at the end or the beginning,
                            pointerLocation = rng.nextInt(ruleList[i].length()); // generate a new random pointer location
                            direction = rng.nextBoolean(); //and direction
                            bracket1Found = false;
                        } else {
                            if (ruleList[i].charAt(pointerLocation) == '[') {
                                if (!bracket1Found) { //found an opening bracket

                                    direction = false; //move right
                                    bracket1Found = true;
                                    bracket1Location = pointerLocation;
                                    pointerLocation++;
                                } else {
                                    if (direction == true) { //found a bracket set
                                        bracket2Location = pointerLocation;
                                        bracket2Found = true;
                                    } else { //found another opening bracket, start looking for this bracket's closing bracket instead
                                        bracket1Location = pointerLocation;
                                        pointerLocation++;
                                    }
                                }
                            } else if (ruleList[i].charAt(pointerLocation) == ']') {
                                if (bracket1Found == false) { //found a closing bracket
                                    direction = true; //left
                                    bracket1Found = true;
                                    bracket1Location = pointerLocation;
                                    pointerLocation--;
                                } else {
                                    if (direction == false) { //found a bracket set
                                        bracket2Location = pointerLocation;
                                        bracket2Found = true;
                                    } else { //found another closing bracket, start looking for this bracket's opening bracket instead
                                        bracket1Location = pointerLocation;
                                        pointerLocation--;
                                    }
                                }
                            } else { //any character besides a bracket
                                if (pointerLocation == (ruleList[i].length() - 1)) { //if pointer is at the end
                                    direction = true; //go left
                                } else if (pointerLocation == 2) { //if its at the beginning of the right side of the rule
                                    direction = false; //go right
                                } else {
                                    if (direction == true) {
                                        pointerLocation--; //move pointer to the left
                                    } else {
                                        pointerLocation++; //move pointer to the right
                                    }
                                }
                            }
                        }
                    }
                    else{
                        bracket2Found = true;
                        noReplacements = true;
                    }
                }
                if ((ruleList[i].length() > 1) && (noReplacements = false)) {
                    if (bracket1Location < bracket2Location) { //if bracket1 is an opening bracket
                        if (bracket1Location == 2) { //if the opening bracket is the first character on the right side of the string
                            ruleList[i] = ruleList[i].substring(0, 1) + ruleList[i].substring((bracket2Location + 1), (ruleList[i].length()) - 1); //set the rule equal to the left side of the rule + "=" + everything after the bracket set
                        } else if (bracket2Location == (ruleList[i].length()-1)) { // if the closing bracket is the last character of the string
                            ruleList[i] = ruleList[i].substring(0, (bracket1Location - 1)); //set the rule equal to everything before the bracket set
                        } else { //if the bracket set is not on either end of the string
                            String ruleHalf1 = ruleList[i].substring(0, (bracket1Location - 1)); //first half of the rule is equal to everything up to the opening bracket
                            String ruleHalf2 = ruleList[i].substring((bracket2Location + 1), (ruleList[i].length() - 1)); //second half of the rule is everything after the closing bracket
                            ruleList[i] = ruleHalf1 + ruleHalf2; //combine them
                        }
                    } else { //if bracket1 is a closing bracket
                        if (bracket1Location == (ruleList[i].length()-1)) { //if bracket1 is the last character of the string
                            ruleList[i] = ruleList[i].substring(0, (bracket2Location - 1)); //set the rule equal to everything before bracket2
                        } else if (bracket2Location == 2) { //if bracket1 is the first character of the string
                            ruleList[i] = ruleList[i].substring(0, 1) + ruleList[i].substring((bracket1Location + 1), (ruleList[i].length() - 1)); //set the rule equal to the left side of the rule +  "=" + everything after the bracket set
                        } else { //if the bracket set is not on either end of the string
                            String ruleHalf1 = ruleList[i].substring(0, (bracket2Location - 1)); //first half of the rule is equal to everything up to the opening bracket
                            String ruleHalf2 = ruleList[i].substring((bracket1Location + 1), (ruleList[i].length() - 1)); //second half of the rule is everything after the closing bracket
                            ruleList[i] = ruleHalf1 + ruleHalf2; //combine them
                        }
                    }
                } else {
                    ruleList[i] = ruleList[i];
                }
            }

            }
            if (!noReplacements) {
                int numNewBComponents = rng.nextInt(2) + 1; //adds 1-2 new bcomponents
                for (int g = 0; g < numNewBComponents; g++) {
                    int randomPosition = rng.nextInt((ruleList[i].length()));
                    if (randomPosition <= 2) //if random position isn't on the right side of the rule
                        randomPosition = 2; //set it to the first character of the right side
                    if (ruleList[i].length() <= 3) { //if the right side of the string is only one character long
                        ruleList[i] = ruleList[i].substring(0, 1) + bComponents[rng.nextInt(11)]; //replace it with a random bComponent
                    } else {
                        if ((randomPosition + 1) == (ruleList[i].length())) { //if the randomly selected position is the last character of the string
                            ruleList[i] = ruleList[i] + bComponents[rng.nextInt(11)]; //add a random bcomponent to the end of the string
                        } else { //if it's in the middle
                            ruleList[i] = ruleList[i].substring(0, randomPosition) + bComponents[rng.nextInt(11)] + (ruleList[i].substring((randomPosition + 1), (ruleList[i].length() - 1))); //insert random bcomponent into string
                        }
                    }
                }
            }

        }


        return ruleList;

    }

    public int mutateTurnAngle(int turnAngle){
        Random rng = new Random();
        int angleChange = rng.nextInt(5);
        boolean angleDirection = rng.nextBoolean(); // true = +, false = -
        if (mutationProbability.nextEvent(rng)){
            if (angleDirection == true){
                turnAngle += angleChange;
            }
            else {
                turnAngle -= angleChange;
            }
        }
        return turnAngle;
    }

    public String[] generateFComponents() { //generates the small substrings ("f+f+f" or "ff--g+h" for example) that are combined with bracket prefabs to create random rules.
        Random rng = new Random();
        String[] fComponents = new String[100]; //100 unique components gives a high range of generative posibilities
        for (int i = 0; i < 99; i++){ //for each element in the array, generate a component and set that element equal to that component.
            int compSize = rng.nextInt(5) + 1; //each component is 1-6 characters long
            String comp = ""; //start with a null component
            for (int j = 0; j < compSize; j ++){ //add a semi-random character until the desired component size is reached;
                int letterOrSymbol = rng.nextInt(100) + 1; //used to make letters more likely than + or - symbols
                if (letterOrSymbol > 30) { //70% chance of letters, 30% chance of + or -
                    comp += fSubComponents[rng.nextInt(3)]; // add f, g or h
                }
                else{
                    boolean plusOrMinus = rng.nextBoolean(); //used to randomly determine if plus or minus is used; true = + | false = -
                    if (plusOrMinus == true){
                        comp += "+";
                    }
                    else{
                        comp += "-";
                    }
                }
            }
            fComponents[i] = comp;
        }
        return fComponents;
    }

    public String[] generateBComponents() { //combines randomly generated fcomponents with bracket prefabs such as "[]" or "[[[]]]" to create the final components that can be randomized to generate rules
        Random rng = new Random();
        String[] fComponents = generateFComponents();
        String[] bComponents = new String[]{fComponents[rng.nextInt(100)], //a single random fcomponent
                "[" + fComponents[rng.nextInt(100)] + "]" + fComponents[rng.nextInt(100)], // [X][X] where X is equal to random fcomponent
                fComponents[rng.nextInt(100)] + "[" + fComponents[rng.nextInt(100)], // X[X]
                "[[" + fComponents[rng.nextInt(100)] + "][" + fComponents[rng.nextInt(100)] + "]]", // [[X]+X]]
                "[" + fComponents[rng.nextInt(100)] + "[" + fComponents[rng.nextInt(100)] + "][" + fComponents[rng.nextInt(100)] + "]]", // [X[X][X]]
                "[[" + fComponents[rng.nextInt(100)] + "]" + fComponents[rng.nextInt(100)] + "[" + fComponents[rng.nextInt(100)] + "]]", // [X]X[X]]
                "[[" + fComponents[rng.nextInt(100)] + "][" + fComponents[rng.nextInt(100)] + "]" + fComponents[rng.nextInt(100)] + "]", // [[X][X]X]
                "[" + fComponents[rng.nextInt(100)] + "[" + fComponents[rng.nextInt(100)] + "]" + fComponents[rng.nextInt(100)] + "[" + fComponents[rng.nextInt(100)] + "]]", // [X[X]X[X]]
                "[" + fComponents[rng.nextInt(100)] + "[" + fComponents[rng.nextInt(100)] + "]" + fComponents[rng.nextInt(100)] + "[" + fComponents[rng.nextInt(100)] + "]" + fComponents[rng.nextInt(100)] + "]", // [X[X]X[X]X]
                "[" + fComponents[rng.nextInt(100)] + "[" + fComponents[rng.nextInt(100)] + "[" + fComponents[rng.nextInt(100)] + "]" + fComponents[rng.nextInt(100)] + "]" + fComponents[rng.nextInt(100)] + "]", // [X[X[X]X]X]
                "[" + fComponents[rng.nextInt(100)] + "[" + fComponents[rng.nextInt(100)] + "[" + fComponents[rng.nextInt(100)] + "]]]", // [X[X[X]]]
                "[[[" + fComponents[rng.nextInt(100)] + "]" + fComponents[rng.nextInt(100)] + "]" + fComponents[rng.nextInt(100)] + "]"}; // [[[X]X]X]
        return bComponents;
    }
}
