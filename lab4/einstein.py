#!/usr/bin/env python
#########
from ortools.sat.python import cp_model


# this function needs to create the csp model and return the model and a list of variables in the model
def setup_csp():
    model = cp_model.CpModel()
    variables = []
    colors = ["Red", "Green", "Ivory", "Yellow", "Blue"]
    nations = ["Englishman", "Spaniard", "Norwegian", "Ukrainian", "Japanese"]
    cigarettes = ["Old Gold", "Kools", "Chesterfields", "Lucky Strike", "Parliaments"]
    drinks = ["Water", "Orange juice", "Tea", "Coffee", "Milk"]
    pets = ["Zebra", "Dog", "Fox", "Snails", "Horse"]

    # TODO: 1. Create all variables and add them to vars!
 	# e.g.,
    # v1 =  model.NewIntVar(1, 5, "variable1")
    # variables.append(v1)
    # v2 =  model.NewIntVar(1, 5, "variable2")
    # variables.append(v2)
    

    # TODO: 2. Add the constraints to the model!
    # You might need model.Add(), model.addAbsEquality() and model.AddAllDifferent()
    # see https://developers.google.com/optimization/reference/python/sat/python/cp_model
    # e.g.,
    # model.Add(v1 == v2)
    # model.Add(v1 != v2)
    # model.Add(v1 == v2 + 2)
    # model.addAbsEquality(2, v1 - v2) # meaning that abs(v1-v2) == 2
    # etc.
        
    # The Englishman lives in the red house.
    # The Spaniard owns the dog.
    # Coffee is drunk in the green house.
    # The Ukrainian drinks tea.
    # The green house is immediately to the right of the ivory house.
    # The Old Gold smoker owns snails.
    # Kools are smoked in the yellow house.
    # Milk is drunk in the middle house.
    # The Norwegian lives in the first house.
    # The man who smokes Chesterfields lives in the house next to the man with the fox.
    # Kools are smoked in the house next to the house where the horse is kept.
    # The Lucky Strike smoker drinks orange juice.
    # The Japanese smokes Parliaments.
    # The Norwegian lives next to the blue house.
    # Each of the five houses has a different color, each of the five inhabitants has a different nationality, prefers a different brand of cigarettes, a different drink, and owns a different pet.

    return model, variables


##############

def solve_csp():
    # create the model
    model, variables = setup_csp()
    # create the solver
    solver = cp_model.CpSolver()
    solution_printer = cp_model.VarArraySolutionPrinter(variables)
    # find all solutions and print them out
    status = solver.SearchForAllSolutions(model, solution_printer)
    if status == cp_model.INFEASIBLE:
        print("ERROR: Model does not have a solution!")
    elif status == cp_model.MODEL_INVALID:
        print("ERROR: Model is invalid!")
        model.Validate()
    elif status == cp_model.UNKNOWN:
        print("ERROR: No solution was found!")
    else:
        n = solution_printer.solution_count
        print("%d solution(s) found." % n)
        print(solver.ResponseStats())
        if n > 1:
            print("ERROR: There should just be one solution!")


##############

def main():
    solve_csp()


if __name__ == "__main__":
    main()
