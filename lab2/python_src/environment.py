from enum import IntEnum
import random
import itertools

##############

class Orientation(IntEnum):
  NORTH = 0
  EAST = 1
  SOUTH = 2
  WEST = 3

  # this allows things like: Orientation.NORTH + 1 == Orientation.EAST
  def __add__(self, i):
    orientations = list(Orientation)
    return orientations[(int(self) + i) % 4]

  def __sub__(self, i):
    orientations = list(Orientation)
    return orientations[(int(self) - i) % 4]

##############

class State:
  # Note, that you do not necessarily have to use this class if you find a
  # different data structure more useful as state representation.

  # TODO: add other attributes that store necessary information about a state of the environment
  #       Only information that can change over time should be kept here.
  turned_on = False
  current_orientation = Orientation.NORTH
  current_x = 0
  current_y = 0
  dirts = []

  def __init__(self, turned_on, inital_position, initial_direction, inital_dirts):
  # TODO: add other attributes that store necessary information about a state of the environment
    self.turned_on = turned_on
    self.current_orientation = initial_direction
    self.current_x = inital_position[0]
    self.current_y = inital_position[1]
    self.dirts = inital_dirts.copy()

  def __str__(self):
    # TODO: modify as needed
    return f"State \nTurned on: {self.turned_on}\nFacing: {self.current_orientation}\nPosition: {self.current_x}, {self.current_y}\n Dirts left: {sum(self.dirts)}"

  def __hash__(self):
    # TODO: modify as needed
    return hash((
      self.turned_on,
      self.current_orientation,
      self.current_x,
      self.current_y,
      self.dirts
    ))

  def __eq__(self, o):
    # TODO: modify as needed
    if not isinstance(o, State):
      return False

    return (
      self.turned_on == o.turned_on and
      self.current_orientation == o.current_orientation and
      self.current_x == o.current_x and
      self.current_y == o.current_y and
      self.dirts == o.dirts
    )

##############

class Environment:
  # TODO: add other attributes that store necessary information about the environment
  #       Information that is independent of the state of the environment should be here.
  _width = 0
  _height = 0

  def __init__(self, width, height, nb_dirts):
    self._width = width
    self._height = height
    # TODO: randomly initialize an environment of the given size
    # That is, the starting position, orientation and position of the dirty cells should be (somewhat) random.
    # for example as shown here:
    # generate all possible positions
    all_positions = list(itertools.product(range(1, self._width+1), range(1, self._height+1)))
    # randomly choose a home location
    self._home = random.choice(all_positions)
    # randomly choose locations for dirt
    self._dirts = random.sample(all_positions, nb_dirts)

    #Randomly choose a starting orientation
    self._orientation = random.choice(list(Orientation))
    return

  def get_initial_state(self):
    # TODO: return the initial state of the environment
    dirts_length = len(self._dirts)
    dirt_init = [True] * dirts_length
    return State(False, self._home, self._orientation, dirt_init)

  def get_legal_actions(self, state: State):
    actions = []
    # TODO: check conditions to avoid useless actions
    if not state.turned_on:
      actions.append("TURN_ON")
    else:
      position = (state.current_x, state.current_y)
      if position == self._home: # should be only possible when agent has returned home
        actions.append("TURN_OFF")
      for i in range(len(self._dirts)):
        if (position == self._dirts[i] and state.dirts[i]):
          actions.append("SUCK")
      #if position in self._dirts: # should be only possible if there is dirt in the current position
      if (state.current_orientation == Orientation.NORTH):
        if (state.current_y < self._height):
          actions.append("GO")
      elif state.current_orientation == Orientation.EAST: # should be only possible when next position is inside the grid (avoid bumping in walls)
        if state.current_x < self._width:
          actions.append("GO")
      elif state.current_orientation == Orientation.SOUTH:
        if state.current_y > 1:
          actions.append("GO")
      elif state.current_orientation == Orientation.WEST:
        if state.current_x > 1:
          actions.append("GO")

      actions.append("TURN_LEFT")
      actions.append("TURN_RIGHT")
    return actions

  def get_next_state(self, state: State, action):
    # TODO: add missing actions
    if action == "TURN_ON":
      return State(True, (state.current_x, state.current_y), state.current_orientation, state.dirts)
    elif action == "TURN_OFF":
      return State(False, (state.current_x, state.current_y), state.current_orientation, state.dirts)
    elif action == "SUCK":
      for i in range(len(self._dirts)):
        if (state.current_x == self._dirts[i][0] and state.current_y == self._dirts[i][1]):
          new_dirts = state.dirts.copy()
          new_dirts[i] = False
          return State(state.turned_on, (state.current_x, state.current_y), state.current_orientation, new_dirts)
    elif action == "GO":
      # Checks orientation and goes one step in the direction
      if (state.current_orientation == Orientation.NORTH):
        return State(state.turned_on, (state.current_x, state.current_y+1), state.current_orientation, state.dirts)
      elif (state.current_orientation == Orientation.EAST):
        return State(state.turned_on, (state.current_x+1, state.current_y), state.current_orientation, state.dirts)
      elif (state.current_orientation == Orientation.SOUTH):
        return State(state.turned_on, (state.current_x, state.current_y-1), state.current_orientation, state.dirts)
      elif (state.current_orientation == Orientation.WEST):
        return State(state.turned_on, (state.current_x-1, state.current_y), state.current_orientation, state.dirts)
    elif action == "TURN_LEFT":
      return State(state.turned_on, (state.current_x, state.current_y), state.current_orientation-1, state.dirts)
    elif action == "TURN_RIGHT":
      return State(state.turned_on, (state.current_x, state.current_y), state.current_orientation+1, state.dirts)
      
    else:
      raise Exception("Unknown action %s" % str(action))

  def get_cost(self, state: State, action):
    if action == "TURN_OFF":
      dirt_left = sum(state.dirts)
      if (state.current_x, state.current_y) == self._home:
        return 1 + (50 * dirt_left)
      else:
        return 100 + (50 * dirt_left)
    elif action == "SUCK":
      for i in range(len(self._dirts)):
        if ((state.current_x, state.current_y) == self._dirts[i] and state.dirts[i]):
          return 1
        else:
          return 5
    return 1

  def is_goal_state(self, state):
    # TODO: correctly implement the goal test
    position = (state.current_x, state.current_y)
    return (not state.turned_on) and (not any(state.dirts)) and position == self._home

##############

def expected_number_of_states(width, height, nb_dirts):
  # TODO: return a reasonable upper bound on number of possible states
  return 8 * width * height * pow(2, nb_dirts)
