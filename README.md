[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.15271853.svg)](https://doi.org/10.5281/zenodo.15271853)

# taVNS CloseNIT Pilot Project
(transcutaneous auricular Vagus Nerve Stimulation)

## GitHub Repository

[https://github.com/NewcastleRSE/Java_taVNS](https://github.com/NewcastleRSE/Java_taVNS)

This software was developed to capture data from a breathing belt via a National Instruments DAQ and, based on the breathing cycle, deliver stimulation to trigger a digital stimulating device. The software was used and tested on the following devices:                                        
- NI USB-6229
- NI USB-6009
- DigiStim Digitime DSR8

## About

Co-morbid depression is common in people with heart failure (HF) and leads to worse health
outcomes. Autonomic nervous system (ANS) dysregulation, as indexed by reduced heart rate
variability (HRV), is seen in depression and HF. We hypothesise that ANS dysregulation is
mechanistically involved in depression in HF patients, explaining the poor prognosis. Implanted
vagus nerve stimulation (VNS) directly targets the ANS. It is used clinically in depression and
experimentally in HF, consistently improving quality of life (QoL). Transauricular VNS (taVNS) is a
non-invasive alternative. It holds promise but optimal stimulation parameters are not known. Our
collaboration has demonstrated the feasibility of open-loop taVNS and identified parameter-
specific effects on the HRV of healthy volunteers. The respiratory cycle is relevant for the effects
of taVNS on ANS modulation. We propose a proof-of-concept study to develop lab-based closed-
loop taVNS techniques, gated to trigger stimulation at specific points in the respiratory cycle of
healthy volunteers, to determine ANS impact on measures of electroencephalography (EEG), HRV,
blood pressure and continuous performance tests. In parallel we are studying ANS function,
mood, fatigue and QoL in people with depression and HF. Non-invasive ANS modulation is a potential novel therapeutic strategy targeting mood, fatigue and QoL in HF.

### Project Team
- Dr Jannetta Steyn, Newcastle University  ([jannetta.steyn@newcastle.ac.uk](mailto:jannetta.steyn@newcastle.ac.uk))  
- Dr Frances Turner, Newcastle University  ([frances.hutchings@newcastle.ac.uk](mailto:frances.hutchings@newcastle.ac.uk))
- Dr. Tiago da Silva Costa, Newcastle University ([tiago.da-silva-costa@newcastle.ac.uk](mailto:tiago.da-silva-costa@newcastle.ac.uk))

### RSE Contacts
Frances & Jannetta  
RSE Team  
Newcastle University  
([frances.hutchings@newcastle.ac.uk](mailto:frances.hutchings@newcastle.ac.uk))  
([jannetta.steyn@newcastle.ac.uk](mailto:jannetta.steyn@newcastle.ac.uk))

## Built With (Hardware)
- NI USB-6229
- NI USB-6009
- DigiStim Digitime DSR8
- AD Instruments

## Built With (Software)
- Java
- JNA (https://github.com/java-native-access/jna)
- MigLayout
- JFreeChart https://zetcode.com/java/jfreechart/
- JNI4NI https://github.com/davekirkwood/JNI-for-NI-Drivers

## Getting Started

### Prerequisites

- NI USB6229
- An oscilloscope would be handy
- An ESP32 microcontroller for running the breathing simulator would be handy

### Installation



### Running Locally

The application is written in Java and requires OpenJDK 20. The software is packaged into an executable jar using maven.

### Running Tests

How to run tests on your local system.

## Deployment

### Local

Deploying to a production style setup but on the local system. Examples of this would include `venv`, `anaconda`, `Docker` or `minikube`.

### Production

Deploying to the production system. Examples of this would include cloud, HPC or virtual machine.

## Usage

Any links to production environment, video demos and screenshots.

## Program Structure

```mermaid
    C4Context
      title GUI for DAQ recordings

```

## License
-GNU GENERAL PUBLIC LICENSE, Version 3, 29 June 2007

## Citation

Please cite the associated papers for this work if you use this code:

```
@article{xxx2023paper,
  title={Title},
  author={Author},
  journal={arXiv},
  year={2023}
}
```


## Acknowledgements
This work was funded by a grant from the UK Research Councils, EPSRC grant ref. EP/L012345/1, “Example project title, please update”.

