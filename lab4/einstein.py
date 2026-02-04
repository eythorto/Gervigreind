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

    # House colors
    Red = model.NewIntVar(1, 5, "Red")
    variables.append(Red)

    Blue = model.NewIntVar(1, 5, "Blue")
    variables.append(Blue)

    Yellow = model.NewIntVar(1, 5, "Yellow")
    variables.append(Yellow)

    Green = model.NewIntVar(1, 5, "Green")
    variables.append(Green)

    Ivory = model.NewIntVar(1, 5, "Ivory")
    variables.append(Ivory)

    # Nationalities
    Englishman = model.NewIntVar(1, 5, "Englishman")
    variables.append(Englishman)

    Spaniard = model.NewIntVar(1, 5, "Spaniard")
    variables.append(Spaniard)

    Norwegian = model.NewIntVar(1, 5, "Norwegian")
    variables.append(Norwegian)

    Ukrainian = model.NewIntVar(1, 5, "Ukrainian")
    variables.append(Ukrainian)

    Japanese = model.NewIntVar(1, 5, "Japanese")
    variables.append(Japanese)

    # Pets
    Zebra = model.NewIntVar(1, 5, "Zebra")
    variables.append(Zebra)

    Dog = model.NewIntVar(1, 5, "Dog")
    variables.append(Dog)

    Fox = model.NewIntVar(1, 5, "Fox")
    variables.append(Fox)

    Snails = model.NewIntVar(1, 5, "Snails")
    variables.append(Snails)

    Horse = model.NewIntVar(1, 5, "Horse")
    variables.append(Horse)

    # Cigarette brand
    Old_Gold = model.NewIntVar(1, 5, "Old_Gold")
    variables.append(Old_Gold)

    Kools = model.NewIntVar(1, 5, "Kools")
    variables.append(Kools)

    Chesterfields = model.NewIntVar(1, 5, "Chesterfields")
    variables.append(Chesterfields)

    Lucky_Strike = model.NewIntVar(1, 5, "Lucky_Strike")
    variables.append(Lucky_Strike)

    Parliaments = model.NewIntVar(1, 5, "Parliaments")
    variables.append(Parliaments)

    # Drinks
    Water = model.NewIntVar(1, 5, "Water")
    variables.append(Water)

    Orange_juice = model.NewIntVar(1, 5, "Orange_juice")
    variables.append(Orange_juice)

    Tea = model.NewIntVar(1, 5, "Tea")
    variables.append(Tea)

    Coffee = model.NewIntVar(1, 5, "Coffee")
    variables.append(Coffee)

    Milk = model.NewIntVar(1, 5, "Milk")
    variables.append(Milk)

    # The Englishman lives in the red house.
    model.Add(Englishman == Red)

    # The Spaniard owns the dog.
    model.Add(Spaniard == Dog)

    # Coffee is drunk in the green house.
    model.Add(Coffee == Green)

    # The Ukrainian drinks tea.
    model.Add(Ukrainian == Tea)

    # The green house is immediately to the right of the ivory house.
    model.Add(Green == Ivory + 1)

    # The Old Gold smoker owns snails.
    model.Add(Old_Gold == Snails)

    # Kools are smoked in the yellow house.
    model.Add(Kools == Yellow)

    # Milk is drunk in the middle house.
    model.Add(Milk == 3)

    # The Norwegian lives in the first house.
    model.Add(Norwegian == 1)

    # The man who smokes Chesterfields lives in the house next to the man with the fox.
    model.AddAbsEquality(
        1, Chesterfields - Fox
    )  # Chesterfields can either be on the right/left to the fox house

    # Kools are smoked in the house next to the house where the horse is kept.
    model.AddAbsEquality(
        1, Kools - Horse
    )  # Kools can either be on the right/left to the horse house

    # The Lucky Strike smoker drinks orange juice.
    model.Add(Lucky_Strike == Orange_juice)

    # The Japanese smokes Parliaments.
    model.Add(Japanese == Parliaments)

    # The Norwegian lives next to the blue house.
    model.AddAbsEquality(1, Norwegian - Blue)

    # Each of the five houses has a different color, each of the five inhabitants has a different nationality, prefers a different brand of cigarettes, a different drink, and owns a different pet.
    model.AddAllDifferent([Red, Green, Ivory, Yellow, Blue])
    model.AddAllDifferent([Englishman, Spaniard, Norwegian, Ukrainian, Japanese])
    model.AddAllDifferent([Old_Gold, Kools, Chesterfields, Lucky_Strike, Parliaments])
    model.AddAllDifferent([Water, Orange_juice, Tea, Coffee, Milk])
    model.AddAllDifferent([Zebra, Dog, Fox, Snails, Horse])

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
