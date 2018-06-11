#!/bin/bash
mkdir results;
cp Master/JDBCMLNoPool.txt .
cp Master/JDBCMLNOPS.txt .
cp Master/JDBCML1.txt .
cp Master/JDBCML10.txt .
cp Master/SearchServletML1.txt .
cp Master/SearchServletML10.txt .
cp Master/SearchServletMLNOPS.txt .
cp Master/SearchServletMLNoPool.txt .
cat Slave/JDBCMLNoPool.txt >> JDBCMLNoPool.txt
cat Slave/JDBCMLNOPS.txt >> JDBCMLNOPS.txt
cat Slave/JDBCML1.txt >> JDBCML1.txt
cat Slave/JDBCML10.txt >> JDBCML10.txt
cat Slave/SearchServletML1.txt >> SearchServletML1.txt
cat Slave/SearchServletML10.txt >> SearchServletML10.txt
cat Slave/SearchServletMLNOPS.txt >> SearchServletMLNOPS.txt
cat Slave/SearchServletMLNoPool.txt >> SearchServletMLNoPool.txt
python3 findAvg.py < JDBCMLNoPool.txt > results/JDBCNoPoolResult.txt
python3 findAvg.py < JDBCMLNOPS.txt > results/JDBCNoPSResult.txt
python3 findAvg.py < JDBCML1.txt > results/JDBCMLResult1.txt
python3 findAvg.py < JDBCML10.txt > results/JDBCMLResult10.txt
python3 findAvg.py < SearchServletML1.txt > results/SearchMLResult1.txt
python3 findAvg.py < SearchServletML10.txt > results/SearchMLResult10.txt
python3 findAvg.py < SearchServletMLNOPS.txt > results/SearchMLNoPSResult.txt
python3 findAvg.py < SearchServletMLNoPool.txt > results/SearchMLNoPoolResult.txt

rm *.txt
