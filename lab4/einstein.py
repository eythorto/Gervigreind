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
    # Colors, order is "Red", "Green", "Ivory", "Yellow", "Blue" (so c1 is red)
    c1 = model.NewIntVar(1, 5, colors[0])
    c2 = model.NewIntVar(1, 5, colors[1])
    c3 = model.NewIntVar(1, 5, colors[2])
    c4 = model.NewIntVar(1, 5, colors[3])
    c5 = model.NewIntVar(1, 5, colors[4])

    # Nations
    n1 = model.NewIntVar(1, 5, nations[0])
    n2 = model.NewIntVar(1, 5, nations[1])
    n3 = model.NewIntVar(1, 5, nations[2])
    n4 = model.NewIntVar(1, 5, nations[3])
    n5 = model.NewIntVar(1, 5, nations[4])

    #Cigarettes / Smokes
    s1 = model.NewIntVar(1, 5, cigarettes[0])
    s2 = model.NewIntVar(1, 5, cigarettes[1])
    s3 = model.NewIntVar(1, 5, cigarettes[2])
    s4 = model.NewIntVar(1, 5, cigarettes[3])
    s5 = model.NewIntVar(1, 5, cigarettes[4])

    # Drinks
    d1 = model.NewIntVar(1, 5, drinks[0])
    d2 = model.NewIntVar(1, 5, drinks[1])
    d3 = model.NewIntVar(1, 5, drinks[2])
    d4 = model.NewIntVar(1, 5, drinks[3])
    d5 = model.NewIntVar(1, 5, drinks[4])

    # Pets
    p1 = model.NewIntVar(1, 5, pets[0])
    p2 = model.NewIntVar(1, 5, pets[1])
    p3 = model.NewIntVar(1, 5, pets[2])
    p4 = model.NewIntVar(1, 5, pets[3])
    p5 = model.NewIntVar(1, 5, pets[4])


    # TODO: 2. Add the constraints to the model!
    # You might need model.Add(), model.AddAbsEquality() and model.AddAllDifferent()
    # see https://developers.google.com/optimization/reference/python/sat/python/cp_model
    # e.g.,
    # model.Add(v1 == v2)
    # model.Add(v1 != v2)
    # model.Add(v1 == v2 + 2)
    # model.AddAbsEquality(2, v1 - v2) # meaning that abs(v1-v2) == 2
    # etc.
        
    # The Englishman lives in the red house.
    model.add(n1 == c1)
    # The Spaniard owns the dog.
    model.add(n2 == p2)
    # Coffee is drunk in the green house.
    model.add(d4 == c2)
    # The Ukrainian drinks tea.
    model.add(n4 == d3)
    # The green house is immediately to the right of the ivory house.
    model.add(c2 == c3-1)
    # The Old Gold smoker owns snails.
    model.add(s1 == p4)
    # Kools are smoked in the yellow house.
    model.add(s2 == c4)
    # Milk is drunk in the middle house.
    model.add(d5 == 3)
    # The Norwegian lives in the first house.
    model.add(n3 == 1)
    # The man who smokes Chesterfields lives in the house next to the man with the fox.
    model.AddAbsEquality(1, s3 - p3)
    # Kools are smoked in the house next to the house where the horse is kept.
    model.AddAbsEquality(1, s2 - p5)
    # The Lucky Strike smoker drinks orange juice.
    model.add(s4 == d2)
    # The Japanese smokes Parliaments.
    model.add(n5 == s5)
    # The Norwegian lives next to the blue house.
    model.AddAbsEquality(1, n3 - c5)
    # Each of the five houses has a different color, each of the five inhabitants has a different nationality, prefers a different brand of cigarettes, a different drink, and owns a different pet.
    model.add_all_different([s1, s2, s3, s4, s5])
    model.add_all_different([c1, c2, c3, c4, c5])
    model.add_all_different([n1, n2, n3, n4, n5])
    model.add_all_different([d1, d2, d3, d4, d5])
    model.add_all_different([p1, p2, p3, p4, p5])
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
