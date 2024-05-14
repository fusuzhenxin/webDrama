import { defineStore } from 'pinia';
import request from "@/utils/request";

export const useVideoStore = defineStore('video', {
    state: () => ({
        videoName: '',
        videoData: null,
        Descriptions: null,
        numberOfEpisodes: 0,
    }),

    actions: {
        async loadVideoData() {
            try {
                const res = await request.get('/file/Inception', { params: { name: this.videoName } });
                this.videoData = res.data.data;
            } catch (error) {
                console.error('Error loading video data:', error);
            }
        },

        async loadDescription() {
            try {
                const res = await request.get('/details/finAll', { params: { name: this.videoName } });
                this.Descriptions = res.data.data;
            } catch (error) {
                console.error('Error loading description:', error);
            }
        },

        async loadNumberOfEpisodes() {
            try {
                const res = await request.get('/videos/diversity', { params: { name: this.videoName } });
                this.numberOfEpisodes = res.data.data;
            } catch (error) {
                console.error('Error loading number of episodes:', error);
            }
        },
    },
});
