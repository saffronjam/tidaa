clc
clear

p=1;
q=1;

syms x k1 k2

%% i Värdet på k1
f_e = k1 * ( p + 2*q + 2) * x^2;
area = int(f_e, x, 0, 3);
i_sol = solve(area==1, k1);
fprintf("K1: ");
disp(i_sol)

%% ii Väntevärdet
f_e = i_sol * (p + 2*q + 2) * x^2; % Löst version
ii_sol = int(x * f_e, x, 0, 3); % Löst version
fprintf("Väntevärdet: ");
disp(ii_sol)

%% iii Variansen
iii_sol = int((x - ii_sol)^2 * f_e, x, 0, 3);
fprintf("Variansen: ");
disp(iii_sol)

%% iv Värdet på k2
f_n1 = k2 * (3*q + 3) * x;
f_n2 = k2 * (3*q + 3) * (2 - x);
area_1 = int(f_n1, x, 0, 1);
area_2 = int(f_n2, x, 1, 2);
iv_sol = solve(area_1 + area_2 == 1, k2);
fprintf("K2: ");
disp(iv_sol)

%% v Väntevärdet
f_n1 = iv_sol * (3*q + 3) * x; % Löst version
f_n2 = iv_sol * (3*q + 3) * (2 - x); % Löst version
v_sol = int(x * f_n1, x, 0, 1) + int(x * f_n2, x, 1, 2);
fprintf("Väntevärdet: ");
disp(v_sol)

%% vi Variansen
vi_sol = int((x - v_sol)^2 * f_n1, x, 0, 1) + int((x - v_sol)^2 * f_n2, x, 1, 2);
fprintf("Variansen: ");
disp(vi_sol)

%% vii väntevärde & varians
vantevarde_e4_n6 = 4 * ii_sol + 6 * v_sol;
varians_e4_n6 = 4 * iii_sol + 6 * vi_sol;

fprintf("Väntevärde kombi: ")
disp(vantevarde_e4_n6)
fprintf("Varians kombi: ")
disp(varians_e4_n6)





