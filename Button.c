
#include <stdio.h>
#include <string.h>
#include <errno.h>
#include <stdlib.h>
#include <wiringPi.h>

#define	OUT_PIN		5
#define	IN_PIN		1
#define	IN_PIN1		6


static volatile int globalCounter = 0 ;


void myInterrupt (void)
{
  digitalWrite (OUT_PIN, 1) ;
  delay(5000);
  digitalWrite (OUT_PIN, 0) ;


}


int main (void)
{
 

  if (wiringPiSetup () < 0)
  {
    fprintf (stderr, "Unable to setup wiringPi: %s\n", strerror (errno)) ;
    return 1 ;
  }

  pinMode (OUT_PIN, OUTPUT) ;
  pinMode (IN_PIN,  INPUT) ;
  pinMode (IN_PIN,  INPUT) ;

  if (wiringPiISR (IN_PIN, INT_EDGE_BOTH, &myInterrupt) < 0)
  {
    fprintf (stderr, "Unable to setup ISR: %s\n", strerror (errno)) ;
    return 1 ;
  }
  if (wiringPiISR (IN_PIN1, INT_EDGE_BOTH, &myInterrupt) < 0)
  {
    fprintf (stderr, "Unable to setup ISR: %s\n", strerror (errno)) ;
    return 1 ;
  }

  for (;;)
  {
    

  }

  return 0 ;
}
