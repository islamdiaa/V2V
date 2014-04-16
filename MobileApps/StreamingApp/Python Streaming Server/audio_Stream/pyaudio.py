# import pygame
# pygame.init()
# song = pygame.mixer.Sound('far.mp3')
# clock = pygame.time.Clock()
# song.play()
# while True:
#     clock.tick(60)
# pygame.quit()


import pyglet
song = pyglet.media.load('far.mp3')
song.play()
pyglet.app.run()

