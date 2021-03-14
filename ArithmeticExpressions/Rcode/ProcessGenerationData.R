
rawData = read.table("D:\\GEP output 16\\Target_60.0_pop_100_Iteration_1000_opr_mult.txt",header = F, sep = "\n")
solutions <- data.frame()


i = 1
totalRecords <- nrow(rawData)

while(i < totalRecords){
  
  solutions <- rbind(solutions, data.frame(rawData[i,]))
  i = i + 101
  
}

colnames(solutions) <- "Iteration"


splitIteeration <- read.table(text = as.character(solutions$Iteration), sep = ",", stringsAsFactors = F)

colnames(splitIteeration) <- c("Iteration", "GenNum", "Pop size", "Target diff",
                               "BestFitness_CurrentGen", "solution")

splitIteeration$ActualDiff <- (60 - splitIteeration$BestFitness_CurrentGen)

unique(splitIteeration$solution)
unique(splitIteeration$BestFitness_CurrentGen)
