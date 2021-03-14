#plot biomass for each generation 

setwd("D:/BellosData/GF_3/Plot_Biomass")
library(ggplot2)


createBiomassPlot <- function(biomassFile){
  
  generationNo = str_extract(biomassFile, "\\d+")
  
  biomassData = read.table(biomassFile, header = T, stringsAsFactors = F, sep = ",")
  
  plotB<- ggplot(data=biomassData, aes(x=M, y=biomassValue, group=1)) +
    geom_line(colour = "steel blue")+
    geom_point(colour = "steel blue", size = 2) +
    labs(title=paste0("Generation ",generationNo," Biomass"),x="Model", y = "Biomass value") + 
    theme_minimal() +
    ylim(0,2)
  
  ggsave(paste0("D:/BellosData/GF_3/Plot_Biomass/Generation_",generationNo,"_biomass.png"), plotB,
         width = 10, height = 8, dpi = 250)
  
  
  
  rm(biomassDF)
  
}

sTime = Sys.time()

biomassFiles = list.files(pattern = "^Gen_\\d+_biomass.txt$")

lapply(biomassFiles, function(x) createBiomassPlot(x))

eTime = Sys.time()
tTime = eTime - sTime
