import sys

content = open('api/index.ts').read()
stack = []
for i, char in enumerate(content):
    if char == '{':
        stack.append(i)
    elif char == '}':
        if not stack:
            continue
        start = stack.pop()
        line = content.count('\n', 0, start) + 1
        end_line = content.count('\n', 0, i) + 1
        if line == 34:
             print(f"Brace starting at line {line} (app.get proxy) ends at line {end_line}")
        elif line == 75:
             print(f"Brace starting at line {line} (try) ends at line {end_line}")
