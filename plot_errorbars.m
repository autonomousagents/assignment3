clear;clc;

means1 = [ 17.6400, 12.2974, 12.5620];
means2 = [64.4819, 55.9414, 36.3315];

vars1 = [3.0805, 2.4346, 1.9970];
vars2 = [50.1098,  41.1043, 99.9204];

sds1 = sqrt(vars1);
sds2 = sqrt(vars2);

yMin=min(means1-sds1);
yMax=max(means1+sds1);
subplot(1,2,1),errorbar([Inf, means1],[Inf, sds1],'ko','LineWidth',1.5);
hold on;
subplot(1,2,1),plot([1:1:4],[Inf, means1],'ro','LineWidth',1.5);
ylim([yMin-1 yMax+1]);
xlabel('Nr predators');
ylabel('Avg. number time steps');
title(strcat('Means and SDs (9 by 9 grid)'));

yMin=min(means2-sds2);
yMax=max(means2+sds2);
subplot(1,2,2),errorbar([Inf, means2],[Inf, sds2],'ko','LineWidth',1.5);
hold on;
subplot(1,2,2),plot([1:1:4],[Inf, means2],'bo','LineWidth',1.5);
ylim([yMin-1 yMax+1]);
xlabel('Nr predators');
ylabel('Percentage of times predators won');
title(strcat('Means and SDs (9 by 9 grid)'));