clc
clear

a = 1;

syms x y;
q = [x, y];

%% 

% Övergångsmatris
P = [(a+5) / 20, (15 - a) / 20; 9 / 10, 1 / 10];

%% Uppgift a
ekv1 = q * P == q;
ekv2 = x + y == 1;

[X,Y] = equationsToMatrix([ekv1, ekv2], q);
sol = linsolve(X, Y);

disp(sol)


%% Uppgift b

% 1.
p_0 = [0.1 0.9];

fprintf("P1: [%f %f]\n", p_0 * P^1);
fprintf("P2: [%f %f]\n", p_0 * P^2);
fprintf("P3: [%f %f]\n", p_0 * P^3);
fprintf("P50: [%f %f]\n", p_0 * P^50);
fprintf("P100: [%f %f]\n", p_0 * P^100);

fprintf("\n");

% 2.
p_0 = [0.7 0.3];

fprintf("P1: [%f %f]\n", p_0 * P^1);
fprintf("P2: [%f %f]\n", p_0 * P^2);
fprintf("P3: [%f %f]\n", p_0 * P^3);
fprintf("P50: [%f %f]\n", p_0 * P^50);
fprintf("P100: [%f %f]\n", p_0 * P^100);


%% Uppgift c

p_0 = [1, 0];
p_100 = p_0 * P^100;

disp(p_100(2));

