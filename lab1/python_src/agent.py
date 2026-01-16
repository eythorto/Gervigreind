import random

#############

"""Agent acting in some environment"""
class Agent(object):

  def __init__(self):
    return

  # this method is called on the start of the new environment
  # override it to initialise the agent
  def start(self):
    print("start called")
    return

  # this method is called on each time step of the environment
  # it needs to return the action the agent wants to execute as as string
  def next_action(self, percepts):
    print("next_action called")
    return "NOOP"

  # this method is called when the environment has reached a terminal state
  # override it to reset the agent
  def cleanup(self, percepts):
    print("cleanup called")
    return

#############

"""A random Agent for the VacuumCleaner world

 RandomAgent sends actions uniformly at random. In particular, it does not check
 whether an action is actually useful or legal in the current state.
 """
class RandomAgent(Agent):

  def next_action(self, percepts):
    print("perceiving: " + str(percepts))
    actions = ["TURN_ON", "TURN_OFF", "TURN_RIGHT", "TURN_LEFT", "GO", "SUCK"]
    action = random.choice(actions)
    print("selected action: " + action)
    return action

#############

"""Reflex agent"""

class ReflexAgent(Agent):
  def __init__(self):
    self.state = "init"
    self.x = 0
    self.y = 0

  def next_action(self, percepts):
    print(self.state, self.x, self.y)
    if self.state == "init":
      self.state = "FIND_FIRST_WALL"
      return "TURN_ON"
    elif self.state == "FIND_FIRST_WALL":
      if 'BUMP' in percepts:
        self.state = "FIND_CORNER"
        self.x -= 1
        return "TURN_RIGHT"
      else:
        self.x += 1
        return "GO"
    elif self.state == "FIND_CORNER":
      if 'BUMP' in percepts:
        self.y += 1
        self.state = "CLEAN_LEFT"
        return "TURN_RIGHT"
      else:
        self.y -= 1
        return "GO"
    elif self.state == "CLEAN_LEFT":
      if 'DIRT' in percepts:
        return "SUCK"
      elif 'BUMP' in percepts:
        self.state = "TR"
        self.x += 1
        return "TURN_RIGHT"
      else:
        self.x -= 1
        return "GO"
    elif self.state == "TR":
      self.state = "TR2"
      self.y += 1
      return "GO"
    elif self.state == "TR2":
      if 'BUMP' in percepts:
        self.y -= 1
        if self.x < 0:
          self.state = "GO_HOME_RIGHT"
          return "TURN_RIGHT"
        else:
          self.state = "GO_HOME_LEFT"
          return "TURN_LEFT"
      else:
        self.state = "CLEAN_RIGHT"
        return "TURN_RIGHT"
    elif self.state == "CLEAN_RIGHT":
      if 'DIRT' in percepts:
        return "SUCK"
      elif 'BUMP' in percepts:
        self.state = "TL"
        self.x -= 1
        return "TURN_LEFT"
      else:
        self.x += 1
        return "GO"
    elif self.state == "TL":
      self.state = "TL2"
      self.y += 1
      return "GO"
    elif self.state == "TL2":
      if 'BUMP' in percepts:
        self.y -= 1
        if self.x < 0:
          self.state = "GO_HOME_RIGHT"
          return "TURN_RIGHT"
        else:
          self.state = "GO_HOME_LEFT"
          return "TURN_LEFT"
      else:
        self.state = "CLEAN_LEFT"
        return "TURN_LEFT"
    elif self.state == "GO_HOME_LEFT":
      if self.x == 0:
        self.state = "GO_HOME_DOWN"
        return "TURN_LEFT"
      else:
        self.x -= 1
        return "GO"
    elif self.state == "GO_HOME_RIGHT":
      if self.x == 0:
        self.state = "GO_HOME_DOWN"
        return "TURN_RIGHT"
      else:
        self.x += 1
        return "GO"
    elif self.state == "GO_HOME_DOWN":
      if self.y == 0:
        return "TURN_OFF"
      else:
        self.y -= 1
        return "GO"

  def cleanup(self, percepts):
    self.state = "init"
    self.x = 0
    self.y = 0



