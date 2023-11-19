#_____First/Follow computation
def first_of_sequence(sequence,first):
        if not sequence:
            return set()
        first_seq = set()
        for symbol in sequence:
            if symbol not in first:  # Terminal symbol
                return {symbol}
            first_seq |= first[symbol]
            if 'ε' not in first[symbol]:
                break
        else:
            first_seq.add('ε')
        return first_seq
def compute_first(grammar):
    first = {symbol: set() for symbol, _ in grammar}
    while True:
        updated = False
        for non_terminal, productions in grammar:
            for production in productions:
                original_first = first[non_terminal].copy()
                first[non_terminal] |= first_of_sequence(production,first)
                if first[non_terminal] != original_first:
                    updated = True
        if not updated:
            break
    return first
def compute_follow(grammar, start_symbol, first):
    follow = {symbol: set() for symbol, _ in grammar}
    follow[start_symbol].add('$')  # End of input marker

    while True:
        updated = False
        for non_terminal, productions in grammar:
            for production in productions:
                for i, symbol in enumerate(production):
                    if symbol in follow:  # Non-terminal
                        next_symbols = production[i+1:]
                        original_follow = follow[symbol].copy()
                        follow_set = first_of_sequence(next_symbols,first) - {'ε'}
                        follow[symbol] |= follow_set
                        if 'ε' in first_of_sequence(next_symbols,first) or not next_symbols:
                            follow[symbol] |= follow[non_terminal]
                        if follow[symbol] != original_follow:
                            updated = True
        if not updated:
            break
    return follow
def determine_first_follow(grammar,start_symbol):
    first = compute_first(grammar)
    follow = compute_follow(grammar,start_symbol, first)

    print_first_follow(first,follow)
    display_first_follow_tabulate(first,follow)

    return first,follow

#_____Action Table computation
def fill_action_table(grammar, first, follow):
    # Identify non-terminals and terminals
    non_terminals = {non_terminal for non_terminal, _ in grammar}
    terminals = set().union(*[set(rhs) for _, productions in grammar for rhs in productions]) - non_terminals
    terminals.add('$')  # End-of-input symbol

    # Initialize the action table
    action_table = {non_terminal: {terminal: None for terminal in terminals} for non_terminal in non_terminals}

    for non_terminal, productions in grammar:
        for production in productions:
            # Compute the FIRST of the production
            first_of_production = first_of_sequence(production, first)

            for terminal in first_of_production - {'ε'}:
                # Fill the action table for non-epsilon productions
                action_table[non_terminal][terminal] = production

            if 'ε' in first_of_production:
                # Fill the action table for epsilon productions
                for terminal in follow[non_terminal]:
                    action_table[non_terminal][terminal] = production

    display_action_table(action_table)
    display_action_table_tabulate(action_table)
    return action_table

#_____Display the tables
def print_first_follow(first, follow, filename="more/usefull_tools/first_follow.txt"):
    with open(filename, 'w') as file:
        print("First Sets:")
        file.write("First Sets:\n")
        for non_terminal in first:
            first_set = f"FIRST({non_terminal}) = {first[non_terminal]}\n"
            print(first_set, end='')
            file.write(first_set)

        print("\nFollow Sets:")
        file.write("\nFollow Sets:\n")
        for non_terminal in follow:
            follow_set = f"FOLLOW({non_terminal}) = {follow[non_terminal]}\n"
            print(follow_set, end='')
            file.write(follow_set)
def display_first_follow_tabulate(first_sets, follow_sets, filename="more/usefull_tools/first_follow_tabulate.txt"):
    from tabulate import tabulate

    # Combine FIRST and FOLLOW sets for tabulation
    combined_sets = [(nt, ', '.join(first_sets[nt]), ', '.join(follow_sets[nt])) for nt in first_sets]

    # Use tabulate to create a formatted table
    table = tabulate(combined_sets, headers=["Non-Terminal", "FIRST Set", "FOLLOW Set"], tablefmt="grid")

    print(table)

    with open(filename, 'w') as file:
        file.write(table)
def display_action_table_tabulate(action_table, filename='more/usefull_tools/action_table_tabulate.txt'):
    from tabulate import tabulate

    # Prepare data for tabulation
    rows = []
    for non_terminal, rules in action_table.items():
        for terminal, action in rules.items():
            if action is not None:
                rows.append([non_terminal, terminal, action])

    # Use tabulate to create a formatted table
    table = tabulate(rows, headers=["Non-Terminal", "Terminal", "Action"], tablefmt="grid")

    print(table)

    with open(filename, 'w') as file:
        file.write(table)
def display_action_table(action_table, filename='more/usefull_tools/action_table.txt'):
    print("\nAction Table:")
    with open(filename, 'w') as file:
        for key in action_table:
            for subkey in action_table[key]:
                if action_table[key][subkey] is not None:
                    # Joining elements of the list with a comma
                    formatted_values = ','.join(str(v) for v in action_table[key][subkey])
                    line = f"[{key}, {subkey}] -> {formatted_values}\n"
                    print(line, end='')
                    file.write(line)
            file.write("\n")



grammar = [
    ('Program', [['begin', 'Code', 'end']]),
    ('Code', [['ε'], ['InstList']]),
    ('InstList', [['Instruction'], ['Instruction', '...', 'InstList']]),
    ('Instruction', [['Assign'], ['If'], ['While'], ['Print'], ['Read'], ['begin', 'InstList', 'end']]),
    ('Assign', [['VarName', ':=', 'ExprArith']]),
    ('ExprArith', [['SimpleExpr', 'ExprArithTail']]),
    ('ExprArithTail', [['Op', 'ExprArith'], ['ε']]),
    ('SimpleExpr', [['VarName'], ['Number'], ['(', 'ExprArith', ')'], ['-', 'ExprArith']]),
    ('Op', [['+'], ['-'], ['*'], ['/']]),
    ('If', [['if', 'Cond', 'then', 'Instruction', 'else'], ['if', 'Cond', 'then', 'Instruction', 'else', 'Instruction']]),
    ('Cond', [['SimpleCond', 'CondTail']]),
    ('CondTail', [['and', 'Cond'], ['or', 'Cond'], ['{', 'Cond', '}'], ['ε']]),
    ('SimpleCond', [['ExprArith', 'Comp', 'ExprArith']]),
    ('Comp', [['='], ['<']]),
    ('While', [['while', 'Cond', 'do', 'Instruction']]),
    ('Print', [['print', '(', 'VarName', ')']]),
    ('Read', [['read', '(', 'VarName', ')']])
]


first_sets,follow_sets = determine_first_follow(grammar,'Program')
action_table = fill_action_table(grammar, first_sets, follow_sets)

