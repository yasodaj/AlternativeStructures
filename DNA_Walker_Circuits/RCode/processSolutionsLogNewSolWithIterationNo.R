
library(data.table)

dat <- read.table("E:\\SA\\Toy\\NewGridSize_WithAlternativeSolutions\\Run2\\SolutionsLog.txt", 
                  header = T, stringsAsFactors = F)


newSolutions = dat[dat$FileName %like% "SA_NewSol_Iteration_",]

# 
# min(dat$Fitness)
# min(newSolutions$Fitness)
# min(newSolutions$Fitness)
# 
# sort(unique(dat$Fitness))
# 
# x = newSolutions[newSolutions$Fitness == 60,]
# y = newSolutions[newSolutions$Fitness == 60,]
# 
# unique(x$Solution)
# unique(y$Solution)


#unique(newSolutions$Solution)

#as.numeric(gsub(".*_(\\d+).*$", "\\1", x))

newSolutions["IterationNo"] <- 0
stringProcessFunction <- function(x) as.numeric(gsub(".*_(\\d+).*$", "\\1", x))
newSolutions$IterationNo <- mapply(stringProcessFunction, newSolutions$FileName)

write.table(newSolutions, "E:\\SA\\Toy\\NewGridSize\\Run2\\FilteredNewSolutions.txt", quote = F, row.names = F, sep = "\t")

#sections

totSec = round(nrow(newSolutions)/500)

start = 1

for(sec in 1:totSec){
  
  end = start + 99
  
  if(end > nrow(newSolutions)){
    
    end = nrow(newSolutions)
    
  }
  
  temp = newSolutions[c(start:end),]
  
  fileName = paste0("E:\\SA\\Toy\\NewGridSize\\Run2\\FilteredNewSolutions_",sec,"_of_",totSec,".txt")
  
  write.table(temp,fileName , quote = F, row.names = F, sep = "\t")
  
  start = end + 1
  
  
}

#sliding window
totSec = 9

start = 1

for(sec in 1:totSec){
  
  if(sec == 1){
    end = start + 499
  } else{
    end = start + 499 + 1
  }
  
  if(end > nrow(newSolutions)){
    
    end = nrow(newSolutions)
    
  }
  
  temp = newSolutions[c(start:end),]
  
  fileName = paste0("E:\\SA\\Toy\\NewGridSize\\Run2\\FilterednewSolutionsOverlappingSections_",sec,"_of_",totSec,".txt")
  
  write.table(temp,fileName , quote = F, row.names = F, sep = "\t")
  
  start = end - 250
  
  
}


#unique solutions and their count

uniqunewSolutions = as.data.frame(table(newSolutions$Solution))

uniqunewSolutions$IterationIDs <- 0

for(i in 1:nrow(uniqunewSolutions)){
  
  s = as.character(uniqunewSolutions[i,1])
  #rowNumbers = which(datNew$Solution == s, arr.ind = TRUE)
  rowNumbers = newSolutions[newSolutions$Solution==s,]$IterationNo
  uniqunewSolutions[i,3] <- paste(as.character(rowNumbers), collapse=", ")
  
}

uniqunewSolutions$Fitness <- 0

for(i in 1:nrow(uniqunewSolutions)){
  
  s = as.character(uniqunewSolutions[i,1])
  fitness = newSolutions[newSolutions$Solution == s,7][1]
  uniqunewSolutions[i,4] <- fitness
  
}

write.table(uniqunewSolutions, "E:\\SA\\Toy\\NewGridSize\\Run2\\UniquenewSolutions.txt", quote = F, row.names = F, sep = "\t")
png("testInit.png")
plot(newSolutions$IterationNo, newSolutions$Fitness)
dev.off()
getwd()
