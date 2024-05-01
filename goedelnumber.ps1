using namespace System.Collections.Generic;

$ErrorActionPreference = "Stop";

class Inventory {
  hidden [string] $GoedelStateId = "0"
  [Dictionary[string, string]] $SymbolByGodelId = @{}

  [List[State]] $States = @()

  Inventory(
    [string[]] $TapeAlphabet,
    [string] $ZeroSymbol,
    [string] $OneSymbol,
    [string] $BlankSymbol
  ) {
    $this.SymbolByGodelId.Add($ZeroSymbol, "0");
    $this.SymbolByGodelId.Add($OneSymbol, "00");
    $this.SymbolByGodelId.Add($BlankSymbol, "000");

    $GoedlerSymbolId = "0000";
    foreach ($c in $TapeAlphabet) {
      if ($this.SymbolByGodelId.ContainsKey($c)) {
        continue;
      }

      $this.SymbolByGodelId.Add($c, $GoedlerSymbolId);
      $GoedlerSymbolId += "0";
    }
  }

  [State] AddState($unparsedState) {
    $state = [State]::Parse($unparsedState);
    $this.States.Add($state);
    return $state;
  }

  [string] GetGoedelSymbolId([string] $symbol) {
    return $this.SymbolByGodelId[$symbol];
  }

  [string] GetGoedelStateId([string] $stateId) {
    foreach ($s in $this.States) {
		if($s.id -eq $stateId){
			return "0"*$s.Name.replace("q","");
		}
	}
    throw new InvalidOperationException("invalid state");
  }

  hidden [string] NextGoedelStateId() {
    $id = $this.GoedelStateId;
    $this.GoedelStateId += "0";
    return $id;
  }
}

class Transition {
  [string] $SourceId;
  [string] $TargetId;
  [TransitionInstruction[]] $Instructions;

  static [Transition] Parse($t) {
    $inst = [TransitionInstruction]::ParseAll($t.Labels);

    return [Transition]@{
      SourceId = $t.Source;
      TargetId = $t.Target;
      Instructions = $inst;
    };
  }
}

class Move {
  static [Move] $Left = [Move]::new("L");
  static [Move] $Right = [Move]::new("R");
  static [Move] $NoMove = [Move]::new("N");

  [string] $Direction;

  hidden Move([string] $direction) {
    $this.Direction = $direction;
  }

  [string] ToGoedlerString() {
    $s = switch ($this.Direction) {
      "L" { "0" }
      "R" { "00" }
      default { throw [System.NotSupportedException]::("NoOp movement is not supported :(") }
    };

    return $s;
  }
}

class TransitionInstruction {
  [string] $WhenValue;
  [string] $WriteValue;
  [Move] $Move;

  static [TransitionInstruction[]] ParseAll($labels) {
    $instructions = @();
    foreach ($label in $labels) {
      $instructions += [TransitionInstruction]::Parse($label);
    }

    return $instructions;
  }

  static [TransitionInstruction] Parse([string[]]$label) {
    $direction = $label[2];
    $mv = switch ($direction) {
      { $direction -eq [Move]::Left.Direction } { [Move]::Left }
      { $direction -eq [Move]::Right.Direction } { [Move]::Right }
      default { [Move]::NoMove }
    };

    $when = $label[0];
    $write = $label[1];

    return [TransitionInstruction]@{
      WhenValue = $when;
      WriteValue = $write;
      Move = $mv;
    };
  }
}

class State {
  [string] $Id;
  [string] $Name;
  [Transition[]] $Transitions;

  static [State] Parse($stateEntry) {
    $trans = @();
    foreach ($t in $stateEntry.Transitions) {
      $trans += [Transition]::Parse($t);
    }

    return [State]@{
      Id = $stateEntry.Id;
      Name = $stateEntry.Name;
      Transitions = $trans;
    };
  }
}

class Goedler {
  [Inventory] $Inventory;

  hidden Goedler(
    [Inventory] $inventory
  ) {
    $this.Inventory = $inventory;
  }

  static [string] CreateProgram([Inventory] $inventory, [string] $program) {
    $self = [Goedler]::new($inventory);
    return $self.CreateProgramCore($program);
  }

  hidden [string] CreateProgramCore([string] $program) {
    [string[]]$transitions = @();
    foreach ($state in $this.Inventory.States) {
      foreach ($transition in $state.Transitions) {
        foreach ($instruction in $transition.Instructions) {
          $gt = $this.MakeGoedlerTransition($transition, $instruction);
          $transitions += $gt;
        }
      }
    }

    $transitionStr = "1$($transitions -join "11")";
    $programStr = "$($transitionStr)111$($program)";
    return $programStr;
  }

  hidden [string] MakeGoedlerTransition([Transition] $transition, [TransitionInstruction] $instruction) {
    $s = [GoedlerString]@{
      SourceId = $this.Inventory.GetGoedelStateId($transition.SourceId);
      SymbolReadId = $this.Inventory.GetGoedelSymbolId($instruction.WhenValue);
      TargetId = $this.Inventory.GetGoedelStateId($transition.TargetId);
      WriteId = $this.Inventory.GetGoedelSymbolId($instruction.WriteValue);
      MoveId = $instruction.Move.ToGoedlerString();
    };
    return $s.ToString();
  }
}

class GoedlerString {
  [string] $SourceId;
  [string] $SymbolReadId;
  [string] $TargetId;
  [string] $WriteId;
  [string] $MoveId;

  # q1 0 -> q2 1 R
  # 1010100100100 11
  # 1 0 1 0 1 00 1 00 1 00 11
  # 1
  # 0 => 1 (q1)
  # 1 - delimt
  # (WHEN) READ 0
  # 1 - delimt
  # 00 - dest
  # 00 - WRITE (1)
  # 00 - MOVE (R)
  # 11 - END
  [string] ToString() {
    return "$($this.SourceId)1$($this.SymbolReadId)1$($this.TargetId)1$($this.WriteId)1$($this.MoveId)";
  }
}


$RawStates = Get-Content "D:\ZHAW\Y1S2\THIN\Serie4\turingmachine.java\Automaton_power2.json" | ConvertFrom-Json | Select-Object -ExpandProperty automaton | Select-Object -ExpandProperty States;
$BLANK_CHAR = "_";
$ZERO_CHAR = "NOT_IN_USE";
$ONE_CHAR = "|";

$Alphabet = @($ONE_CHAR,  "0", $BLANK_CHAR, 'x')
$Inventory = [Inventory]::new(
  $Alphabet,
  $ZERO_CHAR,
  $ONE_CHAR,
  $BLANK_CHAR
);

foreach ($s in $RawStates) {
  [void]$Inventory.AddState($s);
}

$ProgramInput = "11";
$FullProgramCode = [Goedler]::CreateProgram($Inventory, $ProgramInput);
Write-Host $FullProgramCode;
