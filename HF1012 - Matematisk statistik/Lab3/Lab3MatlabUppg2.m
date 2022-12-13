clc
clear

a = 1;
b = 1;

syms x y z w;

P = [2*x 0.4 0.2 y; 0.2 x 2*y 0.3; (z + b) 0.22 0.33 0.2; 0.2 (w+a) 0.24 0.42];

%% Uppgift a

ekv1 = P(1, 1) + P(1, 2) + P(1, 3) + P(1, 4) == 1;
ekv2 = P(2, 1) + P(2, 2) + P(2, 3) + P(2, 4) == 1;
ekv3 = P(3, 1) + P(3, 2) + P(3, 3) + P(3, 4) == 1;
ekv4 = P(4, 1) + P(4, 2) + P(4, 3) + P(4, 4) == 1;

q = [x, y, z, w];

[A, B] = equationsToMatrix([ekv1, ekv2, ekv3, ekv4], x, y, z, w);
sol = linsolve(A, B);

x = sol(1);
y = sol(2);
z = sol(3);
w = sol(4);
% Definiera P igen med x, y, z och w kända
P = [2*x 0.4 0.2 y; 0.2 x 2*y 0.3; (z + b) 0.22 0.33 0.2; 0.2 (w+a) 0.24 0.42];

fprintf("X: %f\n", x);
fprintf("Y: %f\n", y);
fprintf("Z: %f\n", z);
fprintf("W: %f\n", w);
fprintf("P:\n")
disp(P)

%% Uppgift b
p_0 = [0.2 0.3 0.2 0.3];

fprintf("P1: [%f %f %f %f]\n", p_0 * P^1);
fprintf("P2: [%f %f %f %f]\n", p_0 * P^2);
fprintf("P3: [%f %f %f %f]\n", p_0 * P^3);
fprintf("P50: [%f %f %f %f]\n", p_0 * P^50);
fprintf("P100: [%f %f %f %f]\n", p_0 * P^100);

%% Uppgift c

% Troligen kommer man nå den stationära sannolikhetsvektorn innan t=5743

%% Uppgift d

% Den stationära sannolikhetsvektorn ligger på 3 < t <= 50

%% Uppgift e

syms qx qy qz qw;
q = [qx qy qz qw];

ekv1 = q * P == q;
ekv2 = qx + qy + qz + qw == 1;

[A, B] = equationsToMatrix([ekv1, ekv2], q);
sol = linsolve(A, B);

fprintf("Stationär sannolikhetsvektor %f %f %f %f\n", sol(1), sol(2), sol(3), sol(4));

%% Uppgift f

% Sannolikhetsvektorn stämde överens med t=50 och t=100 vilket bekräftar
% att vi nått den stationära sannolikhetsvektorn mellan 3 < t <=
% 50
