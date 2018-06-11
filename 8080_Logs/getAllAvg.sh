#!/bin/bash
mkdir results;
python3 findAvg.py < JDBCMLNoPool.txt > results/JDBCNoPoolResult.txt
python3 findAvg.py < JDBCMLNOPS.txt > results/JDBCNoPSResult.txt
python3 findAvg.py < JDBCML1.txt > results/JDBCMLResult1.txt
python3 findAvg.py < JDBCML10.txt > results/JDBCMLResult10.txt
python3 findAvg.py < JDBCMLSecure.txt > results/JDBCMLResultSecure.txt
python3 findAvg.py < SearchServletML1.txt > results/SearchMLResult1.txt
python3 findAvg.py < SearchServletML10.txt > results/SearchMLResult10.txt
python3 findAvg.py < SearchServletMLSecure.txt > results/SearchMLResultSecure.txt
python3 findAvg.py < SearchServletMLNOPS.txt > results/SearchMLNoPSResult.txt
python3 findAvg.py < SearchServletMLNoPool.txt > results/SearchMLNoPoolResult.txt
