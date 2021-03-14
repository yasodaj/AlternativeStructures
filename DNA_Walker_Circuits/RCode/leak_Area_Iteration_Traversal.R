
library(ggplot2)
library(plyr)
library(data.table)

install.packages("plyr", dependencies = TRUE)

folderName = "D:\\SA\\Toy\\NewGridSize_10000I\\"

for(i in 1:20){
  
  dat <- read.table(paste0(folderName,"Run",i,"\\SolutionsLog.txt"), 
                    header = T, stringsAsFactors = F)
  
  newSolutions = dat[dat$FileName %like% "SA_NewSol_Iteration_",]
  
  newSolutions["IterationNo"] <- 0
  stringProcessFunction <- function(x) as.numeric(gsub(".*_(\\d+).*$", "\\1", x))
  newSolutions$IterationNo <- mapply(stringProcessFunction, newSolutions$FileName)
  
  newSolutions$Leaks <- paste(newSolutions$Short_leaks,
                              newSolutions$Medium_leaks,
                              newSolutions$Long_leaks,sep = ",")
  
  newSolutionsT <- newSolutions
  newSolutions <- newSolutions[,-c(1,3,4,5,8)]
  

  ##area - leak plot - iteration
  p<-ggplot(data = newSolutions, aes(x = as.factor(Leaks), y = Area,
                                  color = as.integer(IterationNo), group = IterationNo)) +
    geom_point(aes(size = 2)) +
    # geom_line() +
    scale_colour_gradient(name = "IterationNo",
                          low = "blue", high = "red") +
    theme_minimal()+
    ggtitle(paste0("Model Toy - Run ",i," NewSol - Area and Leak variation During Optimisation"))+
    xlab("Leaks")+
    ylab("Area")
  
  ggsave(paste0(folderName,"RunStats\\Run",i,"_AreaLeak_NewSol.jpg"),p,
         height = 7, width = 10)
  
  ##initial solutions
  
  initSolutions = dat[dat$FileName %like% "SA_Init_Iteration_",]
  
  initSolutions["IterationNo"] <- 0
  stringProcessFunction <- function(x) as.numeric(gsub(".*_(\\d+).*$", "\\1", x))
  initSolutions$IterationNo <- mapply(stringProcessFunction, initSolutions$FileName)
  
  initSolutions$Leaks <- paste(initSolutions$Short_leaks,
                               initSolutions$Medium_leaks,
                               initSolutions$Long_leaks,sep = ",")
  
  
  initSolutions <- initSolutions[,-c(1,3,4,5,8)]
  
  p<-ggplot(data = initSolutions, aes(x = as.factor(Leaks), y = Area, 
                                     color = as.integer(IterationNo), group = IterationNo)) +
    geom_point(aes(size = 2)) +
    scale_colour_gradient(name = "IterationNo", 
                          low = "blue", high = "red") +
    theme_minimal()+
    ggtitle(paste0("Model Toy - Run ",i," InitSol - Area and Leak variation During Optimisation"))+
    xlab("Leaks")+
    ylab("Area")
  
  ggsave(paste0(folderName,"RunStats\\Run",i,"_AreaLeak_InitSol.jpg"),p,
         height = 7, width = 10)
  
  
  ##best solutions
  
  masterData<- read.table(paste0(folderName,"Run",i,"\\BestSolutionsLog.txt"), 
                          header = T, stringsAsFactors = F)
  
  eliteData <- masterData[,c(1,3,4)]
  eliteData["Leaks"] <- ""
  eliteData["Area"] <- 0
  
  for(x in 1:nrow(eliteData)){
    
    tempS = eliteData[x,3]
    
    eliteData[x,4] <- initSolutions[initSolutions$Solution == tempS,][5]
    eliteData[x,5] <- initSolutions[initSolutions$Solution == tempS,][2]
    
  }
  
  
  a = unique(eliteData[,c(2:5)])
  a["Iteration"] <- row.names(a)
  
  p<-ggplot(data = a, aes(x = as.factor(Leaks), y = Area, 
                                      color = as.integer(Iteration), group = Iteration)) +
    geom_point(aes(size = 2)) +
    scale_colour_gradient(name = "IterationNo", 
                          low = "blue", high = "red") +
    theme_minimal()+
    ggtitle(paste0("Model Toy - Run ",i," EliteSol - Area and Leak variation During Optimisation"))+
    xlab("Leaks")+
    ylab("Area")
  
  ggsave(paste0(folderName,"RunStats\\Run",i,"_AreaLeak_EliteSol.jpg"),p,
         height = 7, width = 10)
}