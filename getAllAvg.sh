#!/bin/bash
mkdir temp1;
python3 findAvg.py < JDBCMLNoPool.txt > temp1/JDBCNoPoolResult.txt
python3 findAvg.py < JDBCMLNOPS.txt > temp1/JDBCNoPSResult.txt
python3 findAvg.py < JDBCML1.txt > temp1/JDBCMLResult1.txt
python3 findAvg.py < JDBCML10.txt > temp1/JDBCMLResult10.txt
python3 findAvg.py < SearchServletML1.txt > temp1/SearchMLResult1.txt
python3 findAvg.py < SearchServletML10.txt > temp1/SearchMLResult10.txt
python3 findAvg.py < SearchServletMLNOPS.txt > temp1/SearchMLNoPSResult.txt
python3 findAvg.py < SearchServletMLNoPool.txt > temp1/SearchMLNoPoolResult.txt