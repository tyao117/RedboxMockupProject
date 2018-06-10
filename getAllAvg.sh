#!/bin/bash
mkdir temp1;
python3 findAvg.py < JDBCMLNoPool.txt > temp1/JDBCNoPoolResult.txt
python3 findAvg.py < JDBCMLNOPS.txt > temp1/JDBCNoPSResult.txt
python3 findAvg.py < JDBCML.txt > temp1/JDBCMLResult.txt
python3 findAvg.py < SearchServletML.txt > temp1/SearchMLResult.txt
python3 findAvg.py < SearchServletMLNOPS.txt > temp1/SearchMLNoPSResult.txt
python3 findAvg.py < SearchServletMLNoPool.txt > temp1/SearchMLNoPoolResult.txt