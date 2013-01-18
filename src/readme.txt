AUTHORS:
This software was written by David Rawlinson, Alan Zhang, Rajib Chakravorty and
Nhat Vo, all employees of National ICT Australia, between Jan 2011 and Jan 2012.
It is no longer actively developed or supported.

LICENCE:
This software and all source code has been released under the GNU GENERAL PUBLIC LICENSE (Version 2)
A copy of the licence is included in the subdirectory /licences and can be read online at:

http://www.gnu.org/licenses/gpl-2.0.txt

OBJECTIVES:
This software focuses on the total human-hours required to recover cell lineage
from time-lapse microscopy. It uses a combination of image processing and tracking
algorithms to produce an automated solution, and then provides manual tools for
correction of that solution. From our experience this is about 25x faster than
ad-hoc software/manual effort alone. After manual correction, the results are also
very accurate. The software tries to provide good visualization and interaction
tools for cell detection, tracking and lineage reconstruction.

Note that the scope of this software is limited to cell detection, cell tracking,
and lineage inference. It does not try to do anything else.

Dependencies:
This software depends on:
- JDK/JRE 1.6
- Hibernate
- JDBC driver for your SQL database (only tested with Postgres)
- ImageJ (image processing library)
- UJMP (matrix library)

We also used some String utilities written by Kelvin Friedrich.
His source file (BSD licence) is included.

We wrote and build the software in Netbeans 6.9.1.

Some notes on package naming and abbreviations.
- Ct: CellTracking. This provides a quick indicator as to whether a class/object is part of our source
- DB: Database
- ORM: Object-Relational Mapping
- MVC: Model-View-Controller.
- IJ: ImageJ
- TK: Tracking
- MDI: Multi-Document Interface
- UI: User Interface
- CONCRETE: Specific implementations of something


Some notes on design concepts:

ARCHITECTURE:
The system consists of this Java client program, a SQL database, and a filesystem.
The Java program reads and writes permanent state to the SQL database.

DATA PERSISTENCE:
All permanent state is stored in a SQL database, except image files which are
stored on the hard disk in a managed filesystem (managed by this software).

HIBERNATE:
This project uses Hibernate for database access.

DB ENTITIES:
Rather than have many similar database tables of key-value properties for various
entities (e.g. detections, tracks, solutions etc) we have a single set of "entity"
properties stored in generalized entity-properties tables.

SESSIONS:
We keep a single session open for long periods, which can result in a large
hibernate cache. This can slow the program down, but it allows us to manipulate
the database objects directly without hitting the DB too much.

PROPERTIES:
Most user-configurable state is stored in a properties table in the DB.

PAGES:
We have a single frame that is visible continuously and replace the content pane
with various "page" components. We allow the page-graph (ie what pages lead to
what other pages) to be varied at runtime by DB configuration.

EXPERIMENT:
An experiment is a set of images from a time-lapse microscopy experiment. The
images have up to N dimensions (e.g. time, channel, position...)

SOLUTION:
A solution is an attempt to model cell behaviour within an experiment. You may
make several attempts to model parts of an experiment, thus each experiment has
many solutions.

COORDINATES:
Every image in an experiment is described by a set of coordinates in all
dimensions for that experiment.

VIEWPOINT:
A viewpoint is a set of coordinates representing the image[s] currently being
viewed. Different parts of the program (e.g. different windows) may have different
viewpoints simultaneously.

ZOOM CANVAS:
We provide a very nice pan+zoomable super-resolution canvas for displaying images
and overlaying vector graphics.

IMAGE RESULTS:
We provide a graph-based image processing library wrapping ImageJ which delivers
ImageResults objects. These are raster images that result from a pipeline of
processing.

MICROWELLS:
Typically, cell tracking experiments physically partition the observed space to
separate populations of cells. These partitions are called microwells. We provide
tools for describing these to allow separate populations to be modelled
independently.

DETECTIONS:
A detection is the observation of a cell in an image. The cell's perimeter is the
contour of the detection.

TRACKS:
A sequence of observations of a cell over time is called a track.

LINEAGE:
The lineage of a cell is the tracks of itself, its mother, grand-mother etc. and
daughters, grand-daughters and so on. Lineage browsing enables tracking errors
to be easily spotted.

EXPORT:
The export classes are concerned with output as files to be used by other programs.

TIME CONTROL / TIME-LINE:
There is a complex time navigation control. The time control has a slider for
gross movement, video-style playback buttons, and a ticker-tape control for fine
control of time. A window of time may be overlaid on the view, this window is
determined by the time control widget (green circles).

ANNOTATIONS:
Annotations are comments the user can place into specific images. They have a type
and can be moved. They appear on the timeline as balloons of varying colours.

ANNOTATED TRACKING:
In addition to fully automated tracking, we provide a manually-assisted tracking
mode. We call this annotated tracking. The user must give a property "id" to cells
that he/she wishes to track. The user must also specify a "parent" property when
a cell divides: Each daughter must have a "parent" property with the same value
as the parent's "id". If the parent property is left blank, the cell will not be
attached to the lineage (this is ok at the start).

AUTOMATED CELL DETECTION:
We provide 2 methods of automated cell detection. If the cells constantly fluoresce
or otherwise can be thresholded and segmented from the background, you can use the
ThresholdDetectionProcess. If the background has a strong texture and the cells 
move continuously/regularly, you can use the ForegroundDetectionProcess. The latter
works even in the transmission image (bright field). 

Note both methods have a postprocessing step to remove cells that stick together
in clumps. This step assumes the cells are roughly elliptical or at least convex.
This prevents undersegmentation, but would oversegment other cell shapes.

AUTOMATED TRACKING:
The automated tracking method simply uses cell position and a Brownian motion model.
It has extra tweaks concerning plausibility of particular lineages and discovery
of cell divisions. 

Some notes on design patterns / concepts:

- Direct Hibernate manipulation:
We manipulate Hibernate objects directly and keep a large cache. In some circumstances
this is not ideal.

- ObjectDirectory and Singletons:
We used the pattern of storing singleton instances in an Object Directory and
runtime self-assembly of necessary references. This applies to many semi-persistent
global objects.

- PropertyChangeModel:
We used the property change model to signal changes in the model in the Model-View-
Controller pattern. Although some early code was written under a different MVC
pattern... apologies. Generally, we have a model which has a number of listeners.
The listeners include e.g. the View[s]. The controller triggers changes to the
model.

- DB table editor:
We provide a Swing UI for editing tables and fields which are a simple foreign key
to a list table. Also simple field types are supported (e.g. string,numeric).

- EventDispatchThreadProgress:
We provide a method of implementing a modal progress dialog in the event dispatch
thread (Swing).

- ImageResult and ImageOperation:
The program uses a library of image operations wrapping ImageJ. These implement
a graph of dependencies and operations to compute output image[s].

- ZoomCanvas:
The program includes a really nice super-resolution pan + zoomable image viewer
implemented in the ZoomCanvas class.

- Pages:
We use a single persistent Swing JFrame and replace the content pane with a series
of "pages". The graph of page transitions is defined in the database. 